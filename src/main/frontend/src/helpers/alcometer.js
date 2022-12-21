import store from "@/store";
import { useToast } from "vue-toastification";
const toast = useToast();

export async function getAlcometerResult() {
    return await axios.post(`device/alcometer`).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

function defaultError(error) {
    const data = error.response?.data;
    if (data?.message) {
        toast.error(data.message);
    } else {
        switch (error?.response?.status) {
            case 400: toast.error('Ошибка авторизации запроса')
            case 500: toast.error('Ошибка сервера')
            default: toast.error('Неизвестная ошибка запроса')
        }
    }
    
    console.log(error);
}