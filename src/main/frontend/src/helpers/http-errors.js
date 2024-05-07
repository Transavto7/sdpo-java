import store from "@/store";
import { useToast } from "vue-toastification";
const toast = useToast();

export function defaultError(error) {
    const data = error.response?.data;
    if (data?.message) {
        toast.error(data.message);
    } else if (error.response) {
        switch (error?.response?.status) {
            case 400:
                toast.error('Ошибка авторизации запроса');
                break;
            case 500:
                toast.error('Ошибка сервера');
                break;
            default:
                toast.error('Неизвестная ошибка запроса');
        }
    } else {
        // store.state.loseConnect = true;
    }
    console.log(error);
}