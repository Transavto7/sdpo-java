<script>
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";
import {getCar, getDriver} from '@/helpers/api/api';
import { useToast } from "vue-toastification";

export default {
  name: "PrintQrNumPad",
  components: {InputPersonalNumberForm },
  props : {
    type: String
  },
  data() {
    return {
      hash_id: '',
      instance: null,
      error: '',
      toast: useToast(),
      loading: false,
    }
  },
  methods: {
    async start() {
      this.$emit('success-auth', this.instance.hash_id)
    },
    updateHashId(inputPassword) {
      this.hash_id = inputPassword;
    },
    async checkInstance() {
      this.error = null;
      this.instance = null;
      if (this.hash_id.length < 6) {
        return;
      }

      this.loading = true;
      try {
        let instance = null;
        switch (this.type){
          case  'driverId' :  instance = await getDriver(this.hash_id); break;
          case  'carId' :  instance = await getCar(this.hash_id); break;
          default: return;
        }
        if (instance) {
          this.instance = instance
          this.error = null;
        } else {
          this.error = (this.instance === '123' ? 'Водитель' : 'Автомобиль') + ' не найден';
        }
      } catch (error) {
        this.error = error.response?.data?.message || 'Неизвестная ошибка';
      }
      this.loading = false;
    }
  },
  watch: {
    hash_id: function () {
      this.checkInstance();
    }
  },
  computed : {
    title() {
      switch (this.type){
        case  'driverId' :  return this.instance.fio;
        case  'carId' :  return this.instance.gos_number;
        default: return;
      }
    },
    hasInstance() {
      return this.instance !== null
    },
  }
}
</script>

<template>
  <div class="home">
    <div class="driver-form">
      <div v-if="hasInstance" class="driver-form__title animate__animated animate__fadeInDown d-2">
        {{ title }}
      </div>
      <div v-else class="driver-form__title animate__animated animate__fadeInDown d-2">
        Введите ваш идентификатор
      </div>
      <div class="driver-form__input">
        <input type="number" class="animate__animated animate__fadeIn d-5" v-model="hash_id"/>
      </div>

      <input-personal-number-form
          @password=" (inputPassword) => updateHashId(inputPassword)"
      />
      <button v-if="hasInstance"
              @click="start"
              class="btn animate__animated animate__fadeInUp">
        Печать
      </button>
      <div v-if="error" class="driver-form__not-found animate__animated animate__fadeInUp">{{ error }}</div>
    </div>
  </div>
</template>