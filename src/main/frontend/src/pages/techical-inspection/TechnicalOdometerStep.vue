<script>
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";
import {useToast} from "vue-toastification";
import {getEmployee} from "@/helpers/api/employee";
import InputCarNumber from "@/components/InputCarNumber.vue";
import {getCarByNumberOrHash} from "@/helpers/api/technical";
import InputNumber from "@/components/InputNumber.vue";

export default {
  components: {InputNumber, InputCarNumber, InputPersonalNumberForm},
  data() {
    return {
      odometerValue: null,
      error: null,
      toast: useToast(),
    }
  },
  methods: {
    next() {
      this.$store.state.technical.odometer = this.odometerValue;
      this.$store.state.technical.carHashId = this.car.hashId;

      this.$router.push({name: 'technical-result'});
    },
    updateOdometerValue(input) {
      this.odometerValue = input;
      this.checkCarNumber()
    },
    async checkCarNumber() {
      this.error = null;
      this.$store.state.technical.odometer = null;

      if (!this.odometerValue) {
        return;
      }

      if (this.car && this.car.lastOdometerValue) {
        if (this.car.lastOdometerValue > this.odometerValue) {
          this.error = "Новые показания не могут быть меньше предыдущих!";
        }
      }
    }
  },
  computed: {
    car() {
      return this.$store.state.car ?? {};
    },
  }
}
</script>

<template>
  <div class="home">
    <div class="odometer-form">
      <div class="odometer-form__inputs">
        <div class="odometer-form__inputs__description">
          <div class="odometer-form__title animate__animated animate__fadeInDown d-2">Текущие показатели</div>
          <div class="form-card animate__animated animate__fadeInUp d-1">
            <span>Показания одометра</span>
            {{ car.lastOdometerValue ?? 'Нет' }}
          </div>

          <div class="form-card animate__animated animate__fadeInUp d-1">
            <span>Дата последнего ТО</span>
            {{ car.lastInspectionDate ?? 'Нет' }}
          </div>

        </div>
        <div class="odometer-form__inputs__numbers">
          <div class="odometer-form__title animate__animated animate__fadeInDown d-2">
            Введите показания одометра
          </div>
          <div class="odometer-form__input">
            <input type="text"
                   class="animate__animated animate__fadeIn d-5"
                   v-model="odometerValue"/>
          </div>
          <input-personal-number-form
              @password="updateOdometerValue"
          />
        </div>
      </div>
      <div>
        <button v-if="odometerValue && error === null"
                @click="next()"
                class="btn animate__animated animate__fadeInUp mr-2">
          Отправить показания
        </button>
      </div>
      <div v-if="error" class="odometer-form__not-found animate__animated animate__fadeInUp">{{ error }}</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.form-card {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  font-size: 20px;
  font-weight: 500;
  background-color: rgba(#244673, 0.25);
  padding: 0 0 10px;
  border-radius: 10px;
  min-width: 200px;

  span {
    box-sizing: border-box;
    background-color: rgba(#244673, 0.9);
    color: #fff;
    padding: 5px 10px;
    margin-bottom: 10px;
    width: 100%;
    text-align: center;
    border-radius: 10px 10px 0 0;
  }
}

.odometer-form {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  height: calc(100vh - 120px);
  flex-direction: column;
  margin: 0 auto;

  &__inputs {
    display: flex;
    flex-direction: row;
    gap: 20px;

    &__description {
      display: flex;
      flex-direction: column;
      border-right: 1px solid rgba(0, 0, 0, 0.5);
      padding: 0 20px;
      flex-wrap: wrap;
      gap: 25px;
    }

    &__numbers {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      padding: 0 20px;
    }
  }

  &__title {
    color: #3c495c;
    font-size: 1.5em;
    font-weight: 500;
    text-align: center;
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
    outline: none;
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