<script>
import { checkDevices, connectDevice } from '@/helpers/tonometer';
export default {
    data() {
        return {
            result: null,
            connected: '',
            task: null,
        }
    },
    methods: {
      async getDevices() {
        this.task = setInterval(async () => {
            this.result = await checkDevices();
        }, 1000); 
      },
      async connect(address) {
        const connectedAddress = await connectDevice(address);
        console.log(connectedAddress);
      }
    },
    mounted() {
        this.getDevices();
    },
    unmounted() {
        clearInterval(this.task);
    }
}
</script>

<template>
    <div v-if="!result" class="admin__loading">
        <div class="lds-ring"><div></div><div></div><div></div><div></div></div> 
        Загрузка
    </div>

    <div v-else class="admin__tonometer">
        <div class="admin__tonometer-card">
            <div class="admin__tonometer-card-title">
                Информация
            </div> 
            <p>Имя устройства: {{ result?.main?.name || 'Неизвестное устройство'}} </p>
            <p>Адрес устройства: {{ result?.main?.address || 'Неизвестный адрес'}} </p>
            <p>Подключенный тонометр: {{ connected || '-'}} </p>
        </div>
        <div class="admin__tonometer-devices">
            <button class="admin__tonometer-device"
                v-for="device in result?.devices" 
                :key="device.address"
                @click="connect(device.address)"
            >
                {{  device.name || 'Неизвестное устройство' }} - {{ device.address || 'Неизвестный адрес' }}
            </button>
        </div>
    </div>
</template>