<script>
import {
  closeAlcometer,
  getAlcometerResult
} from '@/helpers/alcometer';

export default {
  data() {
    return {
      interval: null,
      seconds: 5,
      needRetry: false,
      showRetry: false,
      statusAlcometer: "FREE",
      recording: false,
    }
  },
  methods: {
    nextStep() {
      this.$router.push({name: 'step-sleep'});
    },
    async prevStep() {
      this.$router.push({name: 'step-thermometer'});
    },
    hasResult(result) {
      return result !== null
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
    await closeAlcometer();
    this.runCountdown()

    this.requestInterval = setInterval(async () => {
      const { status, result} = await getAlcometerResult();
      this.statusAlcometer = status;

      if (!this.hasResult(result)) {
        return;
      }

      this.inspection.alcometer_result = Number(result) || 0;
      this.inspection.alcometer_mode = '1';
      this.nextStep();
    }, 700);
  },
  unmounted() {
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
        <div class="step-alcometer__grid-item">
          <img style="padding-right: 20px" width="650" src="@/assets/images/alcometer-instraction.png">
        </div>
        <div class="step-alcometer__grid-item">
          <div class="step-alcometer__text  animate__animated animate__fadeInUp d-2">
            <h4 v-if="statusAlcometer === 'REQUEST' || statusAlcometer === 'WAIT' || statusAlcometer === 'STOP' || statusAlcometer === 'FREE'">Алкотестер запускается. <br>
              Готовьтесь к продуву.</h4>
            <h4 v-if="statusAlcometer === 'READY'">Алкотестер готов. <br>
              Дуйте в мундштук <br>
              5 секунд до щелчка.</h4>
            <h4 v-if="statusAlcometer === 'ANALYSE' || statusAlcometer === 'RESULT'">Анализ продува. <br>
              Ожидайте перехода <br>
              к следующему этапу.</h4>
            <h4 v-if="statusAlcometer === 'ERROR'">Ошибка продува. <br>
              Перезапускаем алкотестер. <br>
              Подождите.</h4>
          </div>
        </div>
      </div>
    </div>
    <div class="step-buttons">
      <button @click="prevStep()" class="btn opacity blue">Назад</button>
      <button @click="$router.push({ name: 'step-sleep' })" class="btn">Продолжить</button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.step-alcometer__text{
  font-size: 30px;
}
</style>
