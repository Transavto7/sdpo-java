<script>
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";
import {useToast} from "vue-toastification";
import {getEmployee} from "@/helpers/api/employee";
import InputCarNumber from "@/components/InputCarNumber.vue";
import {getCarByNumberOrHash, getLastMedicalInspection} from "@/helpers/api/technical";

export default {
  components: {InputCarNumber, InputPersonalNumberForm},
  data() {
    return {
      employee_id: '',
      employee: null,
      error: '',
      toast: useToast(),
      loading: false,
    }
  },
  mounted() {
    console.log(this.$store.state.technical);
    if (this.$store.state.technical.driver_id) {
      this.$router.push({name: 'technical-login'});
    }
  },
  methods: {
    start() {
      this.$store.state.technical.driver_id = this.employee.driverHashId;
      this.$store.state.technical.type_view = this.employee.typeView;
      this.$store.state.technical.date = this.employee.lastInspectionDate;

      this.$router.push({name: 'technical-login'});
    },
    updateEmployeeId(inputPassword) {
      this.employee_id = inputPassword;
      this.checkEmployee();
    },
    async checkEmployee() {
      this.error = null;

      if (!this.employee_id) {
        this.employee = null;
      }

      if (this.employee_id.length < 6) {
        return;
      }

      this.loading = true;
      try {
        const response = await getLastMedicalInspection(this.employee_id);
        if (response.data) {
          this.employee = response.data
          this.error = null;
        } else if (response.response.data.data.message) {
          this.error = response.response.data.data.message;
        } else {
          this.error = 'Сотрудник не найден';
        }
      } catch (error) {
        this.error = error.response?.data?.message || 'Неизвестная ошибка';
      }

      this.loading = false;
    }
  },
  computed: {
    login() {
      return this.employee;
    },
  }
}
</script>

<template>
  <div class="home">
    <div class="driver-form">
      <div v-if="login" class="driver-form__title animate__animated animate__fadeInDown d-2">
        {{ employee.fio }}
      </div>
      <div v-else class="driver-form__title animate__animated animate__fadeInDown d-2">
        Введите ваш идентификатор
      </div>
      <div class="driver-form__input">
        <input type="number" class="animate__animated animate__fadeIn d-5" v-model="employee_id"
               @input="checkEmployee"/>
      </div>

      <input-personal-number-form
          @password=" (inputPassword) => updateEmployeeId(inputPassword)"
      />

      <div>
        <button v-if="employee"
                @click="start('open')"
                class="btn animate__animated animate__fadeInUp mr-2">
          Продолжить
        </button>
      </div>
      <div v-if="error" class="driver-form__not-found animate__animated animate__fadeInUp">{{ error }}</div>
    </div>
  </div>
</template>