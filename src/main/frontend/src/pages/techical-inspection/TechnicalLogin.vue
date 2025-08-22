<script>
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";
import {useToast} from "vue-toastification";
import {getEmployee} from "@/helpers/api/employee";
import InputCarNumber from "@/components/InputCarNumber.vue";
import {getCarByNumberOrHash} from "@/helpers/api/technical";

export default {
  components: {InputCarNumber, InputPersonalNumberForm},
  data() {
    return {
      car_number: '',
      car: null,
      error: '',
      toast: useToast(),
      loading: false,
    }
  },
  mounted() {
    if (!this.$store.state.technical.driver_id) {
      this.$router.push({name: 'technical-login-mo'});
    }
  },
  methods: {
    start() {
      this.$store.state.car = this.car;
      this.$store.state.technical.car_id = this.car.hashId;
      this.$router.push({name: 'technical-odometer'});
    },
    updateCarNumber(inputPassword) {
      this.car_number = inputPassword;
      this.checkCarNumber()
    },
    async checkCarNumber() {
      this.error = null;

      if (!this.isValidCarNumber) {
        this.car = null;
        return;
      }

      this.loading = true;
      try {
        const {data} = await getCarByNumberOrHash(this.car_number);
        if (data) {
          this.car = data
          this.error = null;
        } else {
          this.error = 'Автомобиль не найден';
        }
      } catch (error) {
        console.log(error)
        this.error = error.response?.data?.message || 'Неизвестная ошибка';
      }

      this.loading = false;
    }
  },
  computed: {
    login() {
      return this.car;
    },
    isValidCarNumber() {
      return /^(?:(?:[АВЕКМНОРСТУХ]\d{3}[АВЕКМНОРСТУХ]{2}\d{2,3})|(?:\d{4,5}[АВЕКМНОРСТУХ]{2}\d{2})|(?:\d{6}))$/.test(this.car_number);
    }
  }
}
</script>

<template>
  <div class="home">
    <div class="car-number-form">
      <div v-if="login" class="car-number-form__title animate__animated animate__fadeInDown d-2">
        {{ car.gosNumber }} ({{ car.markModel }})
      </div>
      <div v-else class="car-number-form__title animate__animated animate__fadeInDown d-2">
        Введите государственный номер автомобиля или идентификатор
      </div>
      <div class="car-number-form__input">
        <input type="text"
               class="animate__animated animate__fadeIn d-5"
               v-model="car_number"/>
      </div>

      <input-car-number
        @input="updateCarNumber"
      />

      <div>
        <button v-if="login"
            @click="start('open')"
                class="btn animate__animated animate__fadeInUp mr-2">
          Начать ТО
        </button>
      </div>
      <div v-if="error" class="car-number-form__not-found animate__animated animate__fadeInUp">{{ error }}</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.car-number-form {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  height: calc(100vh - 70px);
  flex-direction: column;
  margin: 0 auto;

  &__title {
    color: #3c495c;
    font-size: 2em;
    font-weight: 500;
  }

  &__input input {
    width: 710px;
  }

  .number-buttons {
    width: 600px;
  }

  &__not-found {
    margin-top: 25px;
    font-size: 22px;
    font-weight: 500;
    color: #c53936;
  }

  &__input input {
    margin: 20px 0;
    box-sizing: border-box;
    text-align: center;
    caret-color: transparent;
    border: none;
    padding: 10px 20px 10px 40px;
    font-size: 40px;
    letter-spacing: 40px;
    -moz-appearance: textfield;
    font-weight: 500;
    outline:none;
    border-bottom: 1px solid rgba(0, 0, 0, 0.5);
    max-width: 640px;

    &::-webkit-outer-spin-button,
    &::-webkit-inner-spin-button {
      -webkit-appearance: none;
      margin: 0;
    }

    &:focus {
      border-bottom: 1px solid rgba(0, 0, 0, 0.7);
    }
  }

  .btn {
    margin-top: 20px;
    z-index: 3;
  }
}
</style>