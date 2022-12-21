import store from "@/store";
import { useToast } from "vue-toastification";
const toast = useToast();

export async function getDriver(id) {
    return await axios.post(`inspection/${id}`).then(({ data }) => {
        return data?.data?.message;
    }).catch(defaultError);
}

export async function saveInspection(inspection = store.state.inspection) {
    return await axios.post(`inspection/save`, inspection).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

export async function checkConnect(address) {
    return await axios.post(`api/check`, {
        address
    }).then(({ data }) => {
        return data;
    }).catch(error => {
        return false;
    });
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
}
