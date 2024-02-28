import store from "@/store";
import { ErrorCodes } from "vue";
import { useToast } from "vue-toastification";
const toast = useToast();

export async function getAlcometerResult() {
    return await axios.post(`device/alcometer`).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

export async function closeAlcometer() {
    return await axios.post(`device/alcometer/close`).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

export async function enableFastModeAlcometer() {
    return changeMode("fast");
}

export async function enableSlowModeAlcometer() {
       return changeMode("slow");
}

export async function enableModeFromSystemConfig() {
    if (this.$store.state.config?.system.alcometer_fast) return enableFastModeAlcometer();
    if (!this.$store.state.config?.system.alcometer_fast) return enableSlowModeAlcometer();
}

async function changeMode(modeName) {
    return await axios.post(`device/alcometer/change-mode`, {mode: modeName}).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

function defaultError(error) {
    const data = error.response?.data;
    if (data?.message) {
        toast.error(data.message);
    } else if (error.response) {
        switch (error?.response?.status) {
            case 400: toast.error('Ошибка авторизации запроса')
            case 500: toast.error('Ошибка сервера')
            default: toast.error('Неизвестная ошибка запроса')
        }
    } else {
        store.$state.loseConnect = true;
    }
    
    console.log(error);
}