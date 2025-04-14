<script>
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";
import {useToast} from "vue-toastification";
import {getEmployee} from "@/helpers/api/employee";

export default {
  components: {InputPersonalNumberForm},
  data() {
    return {
      employee_id: '',
      employee: null,
      error: '',
      toast: useToast(),
      loading: false,
    }
  },
  methods: {
    start(typeAnketa) {
      this.$store.state.inspection.person_id = this.employee.hash_id;
      this.$store.state.inspection.person_fio = this.employee.fio;
      this.$store.state.inspection.type = 'employee';
      this.$store.state.inspection.type_anketa = typeAnketa;
      this.$router.push({name: 'step-driver'});
    },
    updateEmployeeId(inputPassword) {
      this.employee_id = inputPassword;
      this.checkEmployee();
    },
    async checkEmployee() {
      this.error = null;

      if (this.employee_id.length < 6) {
        return;
      }

      this.loading = true;
      try {
        const employee = await getEmployee(this.employee_id);
        if (employee) {
          this.employee = employee
          this.error = null;
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
    canOpen() {
      return this.employee?.inspection_types.includes('open')
    },
    canClose() {
      return this.employee?.inspection_types.includes('close')
    }
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
        <button v-if="canOpen"
                @click="start('open')"
                class="btn animate__animated animate__fadeInUp mr-2">
          начать смену
        </button>
        <button v-if="canClose"
                @click="start('close')"
                class="btn animate__animated animate__fadeInUp">
          закончить смену
        </button>
      </div>
      <div v-if="error" class="driver-form__not-found animate__animated animate__fadeInUp">{{ error }}</div>
    </div>
  </div>
</template>