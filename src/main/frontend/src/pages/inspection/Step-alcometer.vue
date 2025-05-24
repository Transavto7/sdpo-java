<script>
import {
  closeAlcometer, closeAlcometrSocket,
  enableSlowModeAlcometer,
  getAlcometerResult
} from '@/helpers/alcometer';

export default {
  data() {
    return {
      interval: null,
      seconds: 5,
      needRetry: false,
      showRetry: false,
      statusAlcometer: "",
      statusPrev: "prev",
      statusNow: "now",
      recording: false
    }
  },
  methods: {
    connect() {
      this.connection = new WebSocket("ws://localhost:8080/device/alcometer/status")
      this.connection.onmessage = async (event) => {
        this.statusPrev = this.statusNow;
        this.statusNow = event.data;
      }

      this.connection.onopen = (event) => {
        console.log("Successfully connected to the echo websocket server...")
      }

      this.connection.error = (error) => {
        console.log(error);
      }
    },
    async disconnect() {
      console.log('Disconnect websoket server');
      await closeAlcometrSocket();
    },
    nextStep() {
      this.$router.push({name: 'step-sleep'});
    },
    async prevStep() {
      this.$router.push({name: 'step-thermometer'});
    },
    hasResult(result) {
      return !(result === undefined || result === null || result === 'next');
    },
    runCountdown() {
      this.seconds = 5;
      this.timerInterval = setInterval(() => {
        this.seconds--;
        if (this.seconds < 1) {
          clearInterval(this.timerInterval);
        }
      }, 1000);
    },
  },
  async mounted() {
    await enableSlowModeAlcometer();
    await closeAlcometer();
    this.connect()
    this.runCountdown()

    this.requestInterval = setInterval(async () => {
      const result = await getAlcometerResult();

      if (!this.hasResult(result)) {
        return;
      }

      this.inspection.alcometer_result = Number(result) || 0;
      this.inspection.alcometer_mode = '1';
      this.nextStep();
    }, 700);
  },
  unmounted() {
    this.disconnect()
    clearInterval(this.requestInterval);
    clearInterval(this.timerInterval);
  },
  computed: {
    inspection() {
      return this.$store.state.inspection;
    },
    identifier() {
      return this.$store.state.inspection.driver_id || this.$store.state.inspection.person_id;
    },
    system() {
      return this.$store.state.config?.system || {};
    },
  },
}
</script>
<template>
  <div class="step-alcometer__outer">
    <div class="step-alcometer">
      <h3 class="animate__animated animate__fadeInDown">Количественное определение алкоголя</h3>
      <div class="step-alcometer__items">
        <div class="step-alcometer__item animate__animated animate__fadeInUp d-1">
          <span style="min-height: 30px">3</span>
          <img style="padding-right: 20px" width="80" src="@/assets/images/precise.png">
          Установите мундштук
        </div>
        <div class="step-alcometer__text  animate__animated animate__fadeInUp d-2">
          Установите индивидуальный мундштук<br><br>
          Дождитесь ГОТОВ на экране алкометра<br><br>
          Начните дуть с умеренной силой до<br>
          окончания звукового сигнала.<br><br>
          Снимите индивидуальный мундштук<br><br>
        </div>
      </div>
    </div>
    <div class="step-buttons">
      <button @click="prevStep()" class="btn opacity blue">Назад</button>
      <button @click="$router.push({ name: 'step-sleep' })" class="btn">Продолжить</button>
    </div>
  </div>
</template>
