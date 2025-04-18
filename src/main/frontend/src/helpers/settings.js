import store from '@/store';
import axios from 'axios';
import {getPoint, getVerification} from './api/api';

export async function loadSettings() {
    await axios.post('/setting/load').then(({ data }) => {
        store.state.config = data;

        if (data?.system?.cursor) {
            document.documentElement.classList.remove('disable-mouse');
        }
    }).catch(error => {
        console.log(error);
    })

    store.state.point = await getPoint();
    store.state.verification = await getVerification();
}

export async function syncSettings() {
    await axios.post('/setting/sync').then(({ data }) => {
        store.state.config = data;

        if (data?.system?.cursor) {
            document.documentElement.classList.remove('disable-mouse');
        }
    }).catch(error => {
        console.log(error);
    })

    store.state.point = await getPoint();
    store.state.verification = await getVerification();
}

export async function savePassword(password) {
    await axios.post('/setting/password', {
        password
    }).catch(error => {
        console.log(error);
    });
}

export async function saveSystem(system) {
    await axios.post('/setting/system', system).catch(error => {
        console.log(error);
    });
}

export async function saveAutoSendToCrmFlag(flag) {
    await axios.post(`/setting/system/auto-send-to-crm/${flag}`).catch(error => {
        console.log(error);
    });
}

export async function saveApi(address, token) {
    await axios.post('/setting/api', {
        address,
        token
    }).catch(error => {
        console.log(error);
    });
}

export async function saveTonometerMac(address) {
    await axios.post('/setting/tonometer', {
        address
    }).catch(error => {
        console.log(error);
    });
}

export function getSettings(name) {
    if (store.state.config.system.hasOwnProperty(name)) {
        return JSON.parse(store.state.config.system[name])
    }
    return false;
}

export async function setCursor(isEnable) {
    await axios.post('/setting/temporary/cursor', {
        cursor: isEnable
    }).catch(error => {
        console.log(error);
    });
}