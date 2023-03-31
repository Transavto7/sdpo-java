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


export async function makeMedia() {
    return await axios.post(`device/media`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

export async function makeVideoTest() {
    return await axios.post(`device/video/test`).then(({ data }) => {
        return data;
    }).catch(defaultError);
}

function defaultError(error) {
    if (error.response) {
        store.$state.loseConnect = true;
    }
    
    console.log(error);
}