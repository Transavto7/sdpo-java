import store from '@/store';
import axios from 'axios';
import {getPoint, getVerification} from './api';

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