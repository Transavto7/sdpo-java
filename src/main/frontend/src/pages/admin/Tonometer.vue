<script>
import { checkDevices } from '@/helpers/tonometer';
import { saveTonometerMac } from '@/helpers/settings';
import { useToast } from "vue-toastification";

export default {
    data() {
        return {
            result: null,
            task: null,
            toast: useToast(),
        }
    },
   computed: {
        config() {
            return this.$store.state.config;
        },
    },
    methods: {
      async getDevices() {
        this.task = setInterval(async () => {
            this.result = await checkDevices();
        }, 1000); 
      },
      async save(address) {
        await saveTonometerMac(address);
        this.config.main.tonometer_mac = address;
        this.toast.success('Настройки тонометра сохранены');
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
    <div v-if="!result" class="admin__loading animate__animated animate__fadeInUp">
        <div class="lds-ring"><div></div><div></div><div></div><div></div></div> 
        Загрузка
    </div>

    <div v-else class="admin__tonometer">
  
        <div class="admin__tonometer-card animate__animated animate__fadeInUp d-1">
            <div class="admin__tonometer-card-title">
                Информация
            </div> 
            <p>Имя устройства: {{ result?.main?.name || 'Неизвестное устройство'}} </p>
            <p>Адрес устройства: {{ result?.main?.address || 'Неизвестный адрес'}} </p>
            <p>Подключенный тонометр: {{ config.main.tonometer_mac || '-'}} </p>
        </div>
        <div class="admin__tonometer-devices animate__animated animate__fadeInUp d-3">
            <button class="admin__tonometer-device"
                v-for="device in result?.devices" 
                :disabled="device.address === config.main.tonometer_mac"
                :key="device.address"
                @click="save(device.address)"
            >
                {{  device.name || 'Неизвестное устройство' }} - {{ device.address || 'Неизвестный адрес' }}
            </button>
        </div>
    </div>
</template>