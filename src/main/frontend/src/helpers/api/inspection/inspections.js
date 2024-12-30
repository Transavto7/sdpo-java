import {useToast} from "vue-toastification";
import axios from "axios";

const toast = useToast();

export async function getInspectionFromLocalStorage() {
    return await axios.get(`api/inspection/local/`).then(({data}) => {
        return data;
    }).catch((error) => {
        toast.error('Ошибка получения списка осмотров. Подробности в консоли..')
        console.log(error);
    });
}



