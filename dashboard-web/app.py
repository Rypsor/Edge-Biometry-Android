import streamlit as st
import firebase_admin
from firebase_admin import credentials, firestore
import pandas as pd
import os # Importa el módulo 'os'

# --- Conexión a Firebase (a prueba de despliegues) ---
try:
    # Intenta cargar desde el archivo local (para tu PC)
    if os.path.exists("service-account-key.json"):
        cred_path = "service-account-key.json"
        cred = credentials.Certificate(cred_path)
    else:
        # Si no existe, carga desde los Secrets de Streamlit (para la nube)
        # "firebase_service_account" debe coincidir con el nombre que le diste en Streamlit Secrets
        cred_dict = st.secrets["firebase_service_account"]
        cred = credentials.Certificate(cred_dict)

    firebase_admin.initialize_app(cred)
except ValueError:
    # Evita reinicializar la app
    pass
except Exception as e:
    st.error(f"Error al inicializar Firebase: {e}")
    # Si falla, es probable que el secreto no esté bien configurado en la nube
    st.stop()


db = firestore.client()

# --- Configuración de la Página ---
st.set_page_config(page_title="Dashboard SIOMA (Simulación)", layout="wide")
st.title("🛰️ Dashboard de Sincronización (SIOMA)")
st.write("Visualización de los registros de entrada/salida sincronizados desde la App Android.")

# --- Cargar Datos ---
@st.cache_data(ttl=60) # Cachear por 60 segundos
def load_data():
    logs_ref = db.collection("work_logs").order_by("timestamp", direction=firestore.Query.DESCENDING)
    docs = logs_ref.stream()

    logs_list = []
    for doc in docs:
        log = doc.to_dict()
        log['firebase_id'] = doc.id
        # Firestore convierte las Dates a Timestamps de Google
        if 'timestamp' in log and hasattr(log['timestamp'], 'to_datetime'):
             log['timestamp'] = log['timestamp'].to_datetime()
        logs_list.append(log)

    if not logs_list:
        return pd.DataFrame() # Retorna DF vacío si no hay datos

    df = pd.DataFrame(logs_list)
    # Ordenar columnas para mejor visualización
    columns_order = ['timestamp', 'personId', 'eventType', 'synced', 'id', 'firebase_id']
    # Filtra para solo mostrar columnas que existen
    df = df[[col for col in columns_order if col in df.columns]]
    return df

# --- Mostrar Dashboard ---
df_logs = load_data()

if df_logs.empty:
    st.warning("No se han sincronizado registros desde la App todavía.")
else:
    st.header("Registros Sincronizados")

    # Métrica clave
    total_registros = len(df_logs)
    st.metric(label="Total Registros en la Nube", value=total_registros)

    # Mostrar la tabla de datos
    st.dataframe(df_logs)

    st.header("Estadísticas (Ejemplo)")
    # Gráfico simple de eventos por tipo
    if 'eventType' in df_logs.columns:
        st.bar_chart(df_logs['eventType'].value_counts())

if st.button("Recargar Datos"):
    st.cache_data.clear()
    st.rerun()