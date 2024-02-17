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