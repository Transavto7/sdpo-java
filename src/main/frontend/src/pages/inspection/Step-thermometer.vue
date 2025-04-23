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
      <img class="animate__animated animate__fadeInUp d-1" src="@/assets/images/pirometer2.png">
    </div>

    <div class="step-buttons">
      <button @click="$router.push({ name: 'step-tonometer' })" class="btn opacity blue">Назад</button>

      <button @click="$router.push({ name: 'step-alcometer' })" class="btn">Продолжить</button>
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

.alert-danger {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}
</style>