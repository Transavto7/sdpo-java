<script>
import {
  closeAlcometer,
  enableModeFromSystemConfig,
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
    }
  },
  methods: {
    async runWebCam() {
      if ((JSON.parse(this.system.camera_photo) && !this.inspection.photo)
          || (JSON.parse(this.system.camera_video) && !this.inspection.video)) {
        const data = await makeMedia(this.$store.state.inspection.driver_id);
        this.inspection.photo = data?.photo;
        this.inspection.video = data?.video;
      }
    },
    async stopWebCam() {
      await stopMedia(this.$store.state.inspection.driver_id);
      this.inspection.photo = null;
      this.inspection.video = null;
    },
    async nextStep() {
      this.$router.push({name: 'step-sleep'});
    },
    async prevStep() {
      this.$router.push({name: 'step-thermometer'});
    },
    async retry() {
      this.needRetry = true;
      setTimeout(() => {
        this.showRetry = true;
      }, 3000);
      await this.stopWebCam();
      await enableSlowModeAlcometer();
      await closeAlcometer();
      this.runCountdown();
      // await this.runWebCam();
    },
    hasResult(result) {
      return !(result === undefined || result === null || result === 'next');

    },
    needStartMedia(result) {
      return (result === 'ready' && this.statusAlcometer !== 'ready') || this.hasError(result);
    },
    hasError(result) {
      return result === "error";
    },
    checkReady(result) {
      return result === 'ready';
    },
    checkRetry(result) {
      return this.system.alcometer_fast && this.system.alcometer_retry && Number(result) > 0 && !this.needRetry;
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
    setStatusAlcometerIsReady() {
      this.statusAlcometer = 'ready';
    },
    resetStatusAlcometerIsReady() {
      this.statusAlcometer = '';
    }
  },
  async mounted() {
    await this.runWebCam();
    this.runCountdown()

    this.requestInterval = setInterval(async () => {
      const result = await getAlcometerResult();
      if (!this.hasResult(result)) {
        return;
      }
      if (this.needStartMedia(result)) {
        await this.stopWebCam();
        await this.runWebCam();
        if (this.checkReady(result))  {
          this.setStatusAlcometerIsReady();
        } else {
          this.resetStatusAlcometerIsReady()
        }
        return;
      }
      if (this.checkReady(result)) {
        this.setStatusAlcometerIsReady();
        return;
      } else {
        this.resetStatusAlcometerIsReady()
      }

      if (this.checkRetry(result)) {
        await this.retry();
        return;
      }
      this.inspection.alcometer_result = Number(result) || 0;
      this.inspection.alcometer_mode = this.system.alcometer_fast ? '0' : '1';
      this.nextStep();
    }, 1000);
  },
  unmounted() {
    enableModeFromSystemConfig(this.system.alcometer_fast);
    clearInterval(this.requestInterval);
    clearInterval(this.timerInterval);
  },
  computed: {
    inspection() {
      return this.$store.state.inspection;
    },
    system() {
      return this.$store.state.config?.system || {};
    },
    status() {
      if (this.seconds === 5) {
        return 'через ' + this.seconds + ' секунд';
      } else if (this.seconds === 1) {
        return 'через ' + this.seconds + ' секунду';
      } else if (this.seconds < 1) {
        return ' прямо сейчас!';
      } else {
        return 'через ' + this.seconds + ' секунды';
      }
    }
  },
}
</script>
<template>
  <div class="step-alcometer__outer">
    <div v-if="!showRetry" class="step-alcometer">
      <h3 class="animate__animated animate__fadeInDown">Проверка на алкоголь</h3>

      <div  class="step-alcometer__items">
        <div class="step-alcometer__item animate__animated animate__fadeInUp d-1">
          <span>1</span>
          <img style="padding-right: 20px" width="100" src="@/assets/images/alco_guide.png">
          <label>Проверьте, что вставлена воронка</label>
        </div>
        <div class="step-alcometer__item animate__animated animate__fadeInUp d-2">
          <span>2</span>
          <img width="100" src="@/assets/images/alco_guide_2.png">
          Держите алкотестер на расстоянии 2-3 см от рта
        </div>
        <div class="step-alcometer__text  animate__animated animate__fadeInUp d-2">
          Дождитесь ГОТОВ на экране алкометра<br><br>
          Начните дуть с умеренной силой до окончания<br>
          звукового сигнала.<br><br>
          <p v-if="!needRetry">Дуйте {{ status }}</p>
          <p v-if="needRetry" style="width: 100%">Результат: Положительный</p>
        </div>
      </div>
      <p v-if="!showRetry" class="alert red">
        <i class="ri-alarm-warning-fill"></i>
        НЕ ПРИКАСАТЬСЯ К АЛКОТЕСТЕРУ ГУБАМИ
      </p>
    </div>
    <div v-if="showRetry" class="step-alcometer">
      <h3  class="animate__animated animate__fadeInDown">Количественное определение алкоголя</h3>
      <div  class="step-alcometer__items">
        <div class="step-alcometer__item animate__animated animate__fadeInUp d-1">
          <span style="min-height: 30px">3</span>
          <img style="padding-right: 20px" width="100" src="@/assets/images/alco_guide.png">
          Снимите воронку , установите мундштук
        </div>
        <div class="step-alcometer__text  animate__animated animate__fadeInUp d-2">
          Снимите мундштук-воронка<br><br>
          Установите индивидуальный мундштук<br><br>
          Дождитесь ГОТОВ на экране алкометра<br><br>
          Начните дуть с умеренной силой до окончания<br>
          звукового сигнала.<br><br>
          Снимите индивидуальный мундштук<br><br>
          Установите мундштук-воронка<br><br>
        </div>
      </div>
    </div>
    <div class="step-buttons">
      <button @click="prevStep()" class="btn opacity blue">Назад</button>
      <button @click="nextStep()" v-if="JSON.parse(system.alcometer_skip)" class="btn">Продолжить</button>
    </div>
  </div>
</template>
