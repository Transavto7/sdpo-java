import store from "@/store";
import { useToast } from "vue-toastification";
const toast = useToast();

export async function getDriver(id) {
    return await axios.post(`inspection/${id}`).then(({ data }) => {
        return data?.data?.message;
    }).catch(defaultError);
}

function defaultError(error) {
    switch (error?.response?.status) {
        case 400: toast.error('Ошибка авторизации запроса')
        default: toast.error('Неизвестная ошибка запроса')
    }

    throw new Error('API Error request');
}