import store from '@/store';
import axios from 'axios';

export async function print() {
    return await axios.post(`device/printer`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}


function defaultError(error) {
    const data = error.response?.data;
    
    if (data?.message) {
        toast.error(data.message);
    } else if(data.response) {
        switch (error?.response?.status) {
            case 400: toast.error('Ошибка авторизации запроса')
            default: toast.error('Неизвестная ошибка запроса')
        }
    } else {
        store.$state.loseConnect = true;
    }
    
    console.log(error);
}