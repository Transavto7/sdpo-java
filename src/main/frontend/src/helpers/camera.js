import store from "@/store";
import { useToast } from "vue-toastification";
const toast = useToast();

export async function makePhoto() {
    return await axios.post(`device/photo`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

export async function getSizes() {
    return await axios.get(`device/video/size`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

export async function makeVideo() {
    return await axios.post(`device/video`).then(({ data }) => {
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
            default: toast.error('Неизвестная ошибка запроса')
        }
    }
    
    console.log(error);
}