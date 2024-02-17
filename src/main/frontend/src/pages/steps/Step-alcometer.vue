<script>
import {getAlcometerResult, readyAlcometer} from '@/helpers/alcometer';
import {makeMedia} from '@/helpers/camera';

export default {
  data() {
    return {
      interval: null,
      seconds: 5,
      needRetry: false,
    }
  },
  methods: {
    async saveWebCam() {
      if ((JSON.parse(this.system.camera_photo) && !this.inspection.photo)
          || (JSON.parse(this.system.camera_video) && !this.inspection.video)) {
        const data = await makeMedia(this.$store.state.inspection.driver_id);
        this.inspection.photo = data?.photo;
        this.inspection.video = data?.video;
      }
    },
    async nextStep() {
      this.$router.push({name: 'step-sleep'});
    },
    async prevStep() {
      this.$router.push({name: 'step-thermometer'});
    },
    async retry() {
      this.needRetry = true;
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
    async checkReadyAlcometerRetry() {
      if (this.system.alcometer_retry) {
        if (this.needRetry) {
          return false;
        }
        if (this.needRetry) {
          await readyAlcometer();
          this.runCountdown();
        }
        return true;
      }
    }
  },
  async mounted() {
    await this.saveWebCam();
    await readyAlcometer();
    this.runCountdown()

    this.requestInterval = setInterval(async () => {
      if (!await this.checkReadyAlcometerRetry()) return;
      const result = await getAlcometerResult();
      if (!this.hasResult(result)) return;

      if (this.system.alcometer_retry && Number(result) > 0 && !this.needRetry) {
        await this.retry();
        return;
      }

      this.inspection.alcometer_result = Number(result) || 0;
      this.nextStep();
    }, 1000);
  },
  unmounted() {
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
  }
}
</script>
<template>
  <div class="step-alcometer__outer">
    <div class="step-alcometer">
      <h3 class="animate__animated animate__fadeInDown">Проверка на алкоголь</h3>

      <div class="step-alcometer__items">
        <div v-if="!needRetry" class="step-alcometer__item animate__animated animate__fadeInUp d-1">
          <span>1</span>
          <img  style="padding-right: 20px" width="100" src="@/assets/images/alco_guide.png">
          <label>Проверьте, что вставлена воронка</label>
        </div>
        <div v-if="!needRetry" class="step-alcometer__item animate__animated animate__fadeInUp d-2">
          <span>2</span>
          <img width="100" src="@/assets/images/alco_guide_2.png">
          Держите алкотестер на расстоянии 2-3 см от рта
        </div>
        <div v-if="needRetry" class="step-alcometer__item animate__animated animate__fadeInUp d-1">
          <span style="min-height: 30px">3</span>
          <img style="padding-right: 20px" width="100" src="@/assets/images/alco_guide.png">
          Снимите воронку , установите мундштук
        </div>
        <div class="step-alcometer__text  animate__animated animate__fadeInUp d-2">
          Дождитесь ГОТОВ на экране алкометра<br><br>
          Начните дуть с умеренной силой до окончания<br>
          звукового сигнала.<br><br>

          Дуйте {{ status }}
        </div>
      </div>
      <p v-if="!needRetry" class="alert red">
        <i class="ri-alarm-warning-fill"></i>
        НЕ ПРИКАСАТЬСЯ К АЛКОТЕСТЕРУ ГУБАМИ
      </p>
    </div>
    <div class="step-buttons">
      <button @click="prevStep()" class="btn opacity blue">Назад</button>
      <button @click="nextStep()" v-if="JSON.parse(system.alcometer_skip)" class="btn">Продолжить</button>
    </div>
  </div>
</template>