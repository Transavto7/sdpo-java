import store from "@/store";
import axios from "axios";
import { useToast } from "vue-toastification";
const toast = useToast();

export async function getDriver(id) {
    return await axios.post(`inspection/${id}`).then(({ data }) => {
        return data?.data?.message;
    }).catch(defaultError);
}

export async function saveInspection(inspection = store.state.inspection) {
    if (store.state.config?.main?.selected_medic.id) {
        inspection.user_id = store.state.config.main.selected_medic.id;
    }
    return await axios.post(`inspection/save`, inspection).then(({ data }) => {
        console.log(data);
        return data;
    }).catch(defaultError);
}

export async function checkConnect(address) {
    return await axios.post(`api/check`, {
        address
    }).then(({ data }) => {
        return data;
    }).catch((error) => {
        if (!error.response) {
            store.$state.loseConnect = true;
        }
    });
}

export async function getPoint() {
    return await axios.get('api/pv').then(({ data }) => {
        return data;
    }).catch((error) => {
        console.log(error);
    });
}

export async function getMedics() {
    return await axios.get('api/medics').then(({ data }) => {
        return data;
    }).catch((error) => {
        console.log(error);
    });
}


export async function close() {
    axios.post('exit').then(({ data }) => {
        
    }).catch((error) => {
        console.log(error);
    });
    
    window.location.reload();
}

export async function saveLogo(logo) {
    return await axios.post('api/logo', {
        logo
    }).then(({ data }) => {
        return data;
    }).catch((error) => {
        console.log(error);
    });
}

export async function saveMedic(medic) {
    return await axios.post('api/medic', medic).then(({ data }) => {
        return data;
    }).catch((error) => {
        console.log(error);
    });
}

function defaultError(error) {
    const data = error.response?.data;
    if (data?.message) {
        toast.error(data.message);
    } else if (error.response) {    
        switch (error?.response?.status) {
            case 400: toast.error('Ошибка авторизации запроса')
            default: toast.error('Неизвестная ошибка запроса')
    
        }
    } else {
        store.$state.loseConnect = true;
    }

    console.log(error);
}
