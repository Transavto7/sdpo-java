import store from "@/store";
import { useToast } from "vue-toastification";
const toast = useToast();

export async function getPressure() {
    return await axios.post(`device/tonometer`).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

export async function disableTonometer() {
    return await axios.post(`device/tonometer/disable`).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

export async function setConnection(status = 'connect') {
    return await axios.post('device/tonometer/connect', {
        status, 
    }).then(({ data }) => {            
        return data;
    }).catch(defaultError);
}

export async function checkDevices() {
    return await axios.post(`device/scan`).then(({ data }) => {
        return data;
    }).catch((error) => {
        console.log(error);
    });
}

function defaultError(error) {
    const data = error.response?.data;
    if (data?.message) {
        toast.error(data.message);
    } else if(error.response) {
        switch (error?.response?.status) {
            case 400: toast.error('Ошибка авторизации запроса')
            default: toast.error('Неизвестная ошибка запроса')
        }
    } else {
        store.$state.loseConnect = true;
    }
    
    console.log(error);
}