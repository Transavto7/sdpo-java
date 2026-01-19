<script>
import {getTemp} from '@/helpers/thermometer';

export default {
  data() {
    return {
      interval: null,
      error: {
        show: false,
        message: true,
      },
    }
  },
  async mounted() {
    this.interval = setInterval(async () => {
      let result = await getTemp();

      if (result === undefined || result === null) {
        return;
      }

      if (result === 'next') {
        return;
      }

      result = Number(result) || 36.6;

      if (result < 35) {
        this.error.show = true;
        this.error.message = "Низкая температура, измерьте еще раз!";
        return;
      }

      if (result >= 37) {
        this.error.show = true;
        this.error.message = "Высокая температура, измерьте еще раз!";
        return;
      }

      this.inspection.t_people = result;
      clearInterval(this.interval);
      this.$router.push({name: 'step-alcometer'});
    }, 1000);

  },
  unmounted() {
    clearInterval(this.interval);
  },
  computed: {
    inspection() {
      return this.$store.state.inspection;
    },
    system() {
      return this.$store.state.config?.system || {};
    }
  }
}
</script>

<template>
  <div class="step-4__outer">
    <div class="step-4">
      <h3 class="animate__animated animate__fadeInUp">Измерение температуры тела</h3>
      <div v-if="error.show" class="alert alert-danger">
        {{ error.message }}
      </div>
      <img class="animate__animated animate__fadeInUp" width="650" src="@/assets/images/thermometer-instruction.png">
      <div class="step-thermometer__text animate__animated animate__fadeInUp">
        <p >Поднесите лоб к верхней левой части терминала на расстояние 3-5 см и нажмите кнопку замер</p>
      </div>
    </div>

    <div class="step-buttons">
      <button @click="$router.push({ name: 'step-tonometer' })" class="btn opacity blue">Назад</button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.alert {
  position: relative;
  padding: .75rem 1.25rem;
  margin-bottom: 1rem;
  border: 1px solid transparent;
  border-radius: .25rem;
  width: 50%;
  height: 5%;
  justify-content: center;
}
.step-4__outer img {
  margin: 0;
}

.step-thermometer__text{
  font-size: 25px;
}

.step-4 h3 {
  margin-bottom: 10px;
}

.alert-danger {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}
</style>