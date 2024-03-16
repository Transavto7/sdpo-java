<script>
import { getDriver } from '@/helpers/api';
import { useToast } from "vue-toastification";
import MedicSelect from '@/components/MedicSelect.vue';
import ApprovalModal from '@/components/ApprovalModal.vue';
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";

export default {
   name: 'Home',
   components: { MedicSelect, ApprovalModal, InputPersonalNumberForm },
   data() {
    return {
        driver_id: '',
        error: '',
        toast: useToast(),
        loading: false,
        processingApproval : false,
        visibleApprovalDoc : false,
    }
   },
   methods: {
    async start() {
        const driver = await getDriver(this.driver_id);
        if (!driver) {
            this.driver_id = '';
            this.toast.error('Водитель с указаным ID не найден');
            return;
        }

        this.$store.state.inspection.driver_id = driver.hash_id;
        this.$store.state.inspection.driver_fio = driver.fio;

        this.$router.push({ name: 'step-driver' });
    },
     updateDriverId(inputPassword) {
       this.driver_id = inputPassword;
     },
    async checkDriver() {
        this.$store.state.inspection = {};
        this.error = null;

        if (this.driver_id.length < 6) {
            return;
        }

        this.loading = true;
        try {
            const driver = await getDriver(this.driver_id);
            if (driver) {
                this.$store.state.inspection.driver_id = driver.hash_id;
                this.$store.state.inspection.driver_fio = driver.fio;
                this.$store.state.driver = driver;
                this.error = null;
            } else {
                this.$store.state.inspection = {};
                this.error = 'Водитель не найден';
            }
        } catch (error) {
            console.log(error.response?.data?.message);
            this.error = error.response?.data?.message || 'Неизвестная ошибка';
            this.$store.state.inspection = {};
        }
        
        this.loading = false;
    }
   },
   watch: {
        driver_id: function (val) {
            this.checkDriver();
            if (!this.hasDriver) this.processingApproval = false;
        }
   },
   computed: {
    config() {
        return this.$store.state.config;
    },
    inspection() {
        return this.$store.state.inspection;
    },
    hasDriver() {
      return this.inspection.driver_id && !this.error
    }
   },
}
</script>

<template>
    <medic-select />
    <approval-modal v-model:visible="visibleApprovalDoc"
                    @close-modal="visibleApprovalDoc = false"
                    @accept="processingApproval = true"
    />
    <div class="home">
        <div class="driver-form">
            <div v-if="inspection.driver_fio" class="driver-form__title animate__animated animate__fadeInDown d-2">
                {{ inspection.driver_fio }}
            </div>
            <div v-else class="driver-form__title animate__animated animate__fadeInDown d-2">
                Введите ваш идентификатор
            </div>
            <div class="driver-form__input">
                <input type="number" class="animate__animated animate__fadeIn d-5" v-model="driver_id" @input="checkDriver" />
            </div>

          <input-personal-number-form
              @password=" (inputPassword) => updateDriverId(inputPassword)"
          />
          <div v-if="hasDriver" class="login__approval-card animate__animated animate__fadeInUp">
            <input type="checkbox" v-model="processingApproval">
              <p>Даю согласие на <ins @click="visibleApprovalDoc = true"> обработку персональных данных </ins></p>
          </div>
            <button v-if="hasDriver && processingApproval" @click="start" class="btn animate__animated animate__fadeInUp">начать осмотр</button>
            <div v-else-if="error" class="driver-form__not-found animate__animated animate__fadeInUp">{{ error }}</div>
        </div>
    </div>
</template>