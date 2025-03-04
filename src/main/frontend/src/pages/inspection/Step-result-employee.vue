<script>
import ResultRepeat from "@/components/ResultRepeat";
import Loader from "@/components/common/Loader";
import {saveEmployeeInspection} from "@/helpers/api/employee";

export default {
  components: {Loader, ResultRepeat},
  data() {
    return {
      backTimeout: null,
      result: {},
      conclusion: {
        admitted: '',
        comments: '',
      },
      notIdentified: 'Не идентифицирован',
      loading: false,
      feedback: null,
      phrase: '',
    }
  },
  async mounted() {
    this.$data.loading = true;

    let global = this;
    let checker = async function () {
      if (global.$store.state.waitRecordMedia) {
        setTimeout(checker, 1000);
      } else {
        global.loading = false;
        await global.save();
        if (global.autoStart) {
          global.backTimeout = global.setTimeoutAndRedirect();
        }
      }
    }
    setTimeout(checker, 1000);
  },
  unmounted() {
    clearTimeout(this.backTimeout);
  },
  methods: {
    getSleepStatus(status) {
      return status === 'Да' ? 'Выспались' : 'Не выспались';
    },
    getPeopleStatus(status) {
      return status === 'Да' ? 'Хорошее' : 'Плохое';
    },
    async save() {
      this.result = await saveEmployeeInspection(); // TODO
      this.conclusion.admitted = this.result.admitted ?? '';
      this.conclusion.comments = this.result.comments ?? '';
    },
    redirectHome() {
      if (this.$route.name === 'step-result-employee') {
        this.$router.push('/');
      }
    },
    redirectRepeat() {
      if (this.$route.name === 'step-result-employee') {
        this.$router.push({name: 'step-retry'});
      }
    },
    setTimeoutAndRedirect() {
      if (this.result.admitted === this.notIdentified) {
        return setTimeout(this.redirectRepeat, this.system.delay_before_retry_inspection);
      }
      return setTimeout(this.redirectHome, this.system.delay_before_redirect_to_main_page);
    },
  },
  computed: {
    autoStart() {
      return this.system.auto_start;
    },
    admitted() {
      return this.result?.admitted === 'Допущен'
    },
    showRetryModal() {
      return this.result.admitted === this.notIdentified;
    },
    getComment() {
      return this.result.comments ?? '';
    },
    inspection() {
      return this.$store.state.inspection;
    },
    system() {
      return this.$store.state.config?.system || {};
    },
    connection() {
      return this.$store.state.connection || false;
    },
  }
  ,
}
</script>

<template>
  <loader v-model:loading="loading"/>
  <result-repeat v-model:visible="showRetryModal"
                 v-model:message-content="getComment"
                 @accept="redirectRepeat()"
  ></result-repeat>
  <div style="display: flex; ">
    <div v-if="!loading" class="step-result">
      <div style="text-align: center;">
        <h3 class="step-result__header_with_result animate__animated animate__fadeInDown">Результаты осмотра:
          <span class="step-result__text animate__animated animate__fadeInUp">
                {{ result.admitted || 'ожидание ответа' }}<br>
            </span></h3>
        <div class="step-result__cards">
          <div v-if="system.tonometer_visible && inspection.hasOwnProperty('tonometer')" class="step-result__card animate__animated animate__fadeInUp d-1">
            <span>Давление</span>
            {{ inspection.hasOwnProperty('tonometer') ? inspection.tonometer : 'Неизвестно' }}
          </div>
          <div v-if="system.tonometer_visible && inspection.hasOwnProperty('pulse')" class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Пульс</span>
            {{ inspection.hasOwnProperty('pulse') ? inspection.pulse : 'Неизвестно' }}
          </div>
          <div v-if="system.alcometer_visible && inspection.hasOwnProperty('alcometer_result')" class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Количество промилле</span>
            {{ inspection.hasOwnProperty('alcometer_result') ? inspection.alcometer_result + ' ‰' : 'Неизвестно' }}
          </div>
          <div v-if="system.thermometer_visible && inspection.hasOwnProperty('t_people')" class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Температура тела</span>
            {{ inspection.hasOwnProperty('t_people') ? inspection.t_people + ' °C' : 'Неизвестно' }}
          </div>
          <div v-if="system.question_sleep && inspection.hasOwnProperty('sleep_status')" class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Сонливость</span>
            {{ inspection.hasOwnProperty('sleep_status') ? getSleepStatus(inspection.sleep_status) : 'Неизвестно' }}
          </div>
          <div v-if="system.question_helth && inspection.hasOwnProperty('people_status')" class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Самочувствие</span>
            {{ inspection.hasOwnProperty('people_status') ? getPeopleStatus(inspection.people_status) : 'Неизвестно' }}
          </div>
        </div>
      </div>
      <div class="step-result__footer">
        <div class="step-result__buttons">
          <button @click="$router.push('/')" class="btn blue animate__animated animate__fadeInUp">В начало</button>
        </div>
      </div>
    </div>
  </div>
</template>