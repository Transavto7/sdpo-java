<script>
import { checkDevices, setConnection } from '@/helpers/tonometer';
import { saveTonometerMac } from '@/helpers/settings';
import { useToast } from "vue-toastification";

export default {
    data() {
        return {
            result: null,
            scan_task: null,
            connect_task: null,
            toast: useToast(),
            connecting: false,
        }
    },
   computed: {
        config() {
            return this.$store.state.config;
        },
    },
    methods: {
      async getDevices() {
        this.scan_task = setInterval(async () => {
            this.result = await checkDevices();
        }, 1000); 
      },
      async save(address) {
        await saveTonometerMac(address);
        this.config.main.tonometer_mac = address;
        this.toast.success('Настройки тонометра сохранены');
      },
      async connect() {
        this.connecting = true;
        this.connect_task = setInterval(async () => {
            let result = await setConnection();
            if (result === 'set') {
                this.connected = true;
                this.connecting = false;
                this.config.main.tonometer_connect = true;
                clearInterval(this.connect_task);
            }
        }, 1000); 
      }
    },
    mounted() {
        this.getDevices();
    },
    unmounted() {
        clearInterval(this.scan_task);
        clearInterval(this.connect_task);
        setConnection("stop");
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
            <p>Подключенный тонометр: {{ config.main.tonometer_mac || '-'}} </p>
            <button :disabled="connecting" :class="{ red: connecting }"
                style="width: 100%; margin-top: 10px;"
                @click="connect" class="btn small blue">Подключить</button>
        </div>
        <div class="admin__tonometer-devices animate__animated animate__fadeInUp d-3">
            <button class="admin__tonometer-device"
                v-for="device in result?.devices" 
                :disabled="device.address === config.main.tonometer_mac"
                :key="device.address"
                @click="save(device.address)"
            >
                {{ device.address || 'Неизвестный адрес' }}
                <template v-if="device.name"> - {{ device.name }}</template>
            </button>
        </div>
    </div>
</template>