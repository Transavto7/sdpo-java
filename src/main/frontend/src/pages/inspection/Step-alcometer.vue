<script>
import {
  closeAlcometer, closeAlcometrSocket,
  enableSlowModeAlcometer,
  getAlcometerResult
} from '@/helpers/alcometer';
import {makeMedia, stopMedia} from '@/helpers/camera';

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
  watch:  {
    statusNow: async function () {
      if (this.isChangeStatus("WAIT")) {
        await this.runWebCam()

      }
      if ((this.isChangeStatus("RESULT") || this.isChangeStatus("ERROR") || this.isChangeStatus("FREE") || this.isChangeStatus("STOP")) && this.recording) {
        await this.stopWebCam()
      }
    }
  },
  methods: {
    isChangeStatus(status) {
       return (this.statusNow === status && this.statusPrev !== status)
    },
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

    async runWebCam() {
      if (!this.recording) {
        this.recording = true;
        const data = await makeMedia(this.identifier);
        this.inspection.photo = data?.photo;
        this.inspection.video = data?.video;
        console.log("start media")
      }
    },
    async stopWebCam() {
      if (this.recording) {
        this.recording = false;
        await stopMedia(this.identifier);
        console.log("stop media")
      }
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
    // await this.runWebCam();
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
    if (this.recording) {
      this.stopWebCam();
    }
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
      <h3  class="animate__animated animate__fadeInDown">Количественное определение алкоголя</h3>
      <div  class="step-alcometer__items">
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
    </div>
  </div>
</template>
