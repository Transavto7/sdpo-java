import store from "@/store";
import { useToast } from "vue-toastification";
const toast = useToast();

export async function checkPulse() {
        await axios.post(`device/tonometer`).then(({ data }) => {
            // if (data == 'next') {
            //     await sleep(1000);
            //     return await checkPulse();
            // }
            
            return data;
        }).catch((error) => {
            defaultError(error);
            return;
        });
}

export async function checkDevices() {
    return await axios.post(`device/scan`).then(({ data }) => {
        return data;
    }).catch(async () => {
        await sleep(5000);
    });
}

export async function connectDevice(address) {
    return await axios.post(`device/tonometer/${address}`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

async function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
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