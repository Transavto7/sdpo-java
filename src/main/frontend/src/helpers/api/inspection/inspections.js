import {useToast} from "vue-toastification";
import axios from "axios";

const toast = useToast();

export async function getInspectionFromLocalStorage() {
    return await axios.get(`inspection/local/`).then(({data}) => {
        return data;
    }).catch((error) => {
        toast.error('Ошибка получения списка осмотров. Подробности в консоли..')
        console.log(error);
    });
}

export async function getEmployeesInspectionFromLocalStorage() {
    return await axios.get(`employees/inspection/local/list`).then(({data}) => {
        return data;
    }).catch((error) => {
        toast.error('Ошибка получения списка осмотров. Подробности в консоли..')
        console.log(error);
    });
}

export async function sendInspectionToCrm(inspection) {
    return await axios.post('api/inspection/local/send/crm', inspection).then(({data}) => {
        return data;
    }).catch((error) => {
        toast.error('Ошибка отправки записи')
        console.log(error);
    })
}



