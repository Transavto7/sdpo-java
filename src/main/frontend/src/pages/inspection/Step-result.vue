<script>
import {saveInspection, replayPrint, replayPrintQr, sendFeedbackAfterInspection, getPhrase} from '@/helpers/api';
import ResultRepeat from "@/components/ResultRepeat";
import Loader from "@/components/common/Loader";
import {getSettings} from "@/helpers/settings";

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
    sendFeedbackToServer() {
      if (this.hasResult && this.feedback !== null) {
        sendFeedbackAfterInspection(this.feedback, this.result.id);
      }
    },
    saveFeedback(feedback) {
      this.feedback = feedback;
    },
    getSleepStatus(status) {
      return status === 'Да' ? 'Выспались' : 'Не выспались';
    },
    getPeopleStatus(status) {
      return status === 'Да' ? 'Хорошее' : 'Плохое';
    },
    async save() {
      this.result = await saveInspection();
      this.phrase = this.result.wish_message ?? null;
      this.conclusion.admitted = this.result.admitted ?? '';
      this.conclusion.comments = this.result.comments ?? '';
    },
    async replayPrint() {
      await replayPrint();
    },

    async replayPrintQr() {
      await replayPrintQr();
    },
    redirectHome() {
      if (this.$route.name === 'step-result') {
        this.$router.push('/');
      }
    },
    redirectRepeat() {
      if (this.$route.name === 'step-result') {
        this.$router.push({name: 'step-retry'});
      }
    },
    setTimeoutAndRedirect() {
      if (this.result.admitted === this.notIdentified) {
        return setTimeout(this.redirectRepeat, this.system.delay_before_retry_inspection);
      }
      return setTimeout(this.redirectHome, this.system.delay_before_redirect_to_main_page);
    },
  }
  ,
  watch: {
    feedback: function (val) {
      console.log(this.hasResult)
      this.sendFeedbackToServer();
    },
    result: function (val) {
      console.log(this.hasResult)
      this.sendFeedbackToServer();
    }
  },
  computed: {
    autoStart() {
      return this.system.auto_start;
    },
    admitted() {
      return this.result?.admitted === 'Допущен'
    }
    ,
    showRetryModal() {
      return this.result.admitted === this.notIdentified;
    }
    ,
    getComment() {
      return this.result.comments ?? '';
    }
    ,
    hasResult() {
      return this.result.admitted !== undefined;
    },
    inspection() {
      return this.$store.state.inspection;
    }
    ,
    system() {
      return this.$store.state.config?.system || {};
    },
    drawReaction() {
      if (!this.hasReaction) {
        return "Пожалуйста, оцените осмотр"
      }
      return this.phrase || "Хорошего дня!";
    },
    hasReaction() {
      return this.feedback !== null
    },
    hasPhrase() {
      console.log(this.phrase !== null)
      return this.phrase !== null
    },
    canRetryPrint() {
      return getSettings('printer_write')
    },
    canRetryPrintQR() {
      return getSettings('print_qr_check')
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
        <!--    <div class="step-result">-->
        <h3 class="animate__animated animate__fadeInDown">Результаты осмотра:
          <span class="step-result__text animate__animated animate__fadeInUp">
                {{ result.admitted || 'ожидание ответа' }}<br>
            </span></h3>
        <div class="step-result__cards">
          <!--        <div v-if="system.tonometer_visible" class="step-result__card animate__animated animate__fadeInUp d-1">-->
          <div class="step-result__card animate__animated animate__fadeInUp d-1">
            <span>Давление</span>
            {{ inspection.hasOwnProperty('tonometer') ? inspection.tonometer : 'Неизвестно' }}
          </div>
          <!--        <div v-if="system.tonometer_visible" class="step-result__card animate__animated animate__fadeInUp d-2">-->
          <div class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Пульс</span>
            {{ inspection.hasOwnProperty('pulse') ? inspection.pulse : 'Неизвестно' }}
          </div>
          <!--        <div v-if="system.alcometer_visible" class="step-result__card animate__animated animate__fadeInUp d-2">-->
          <div class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Количество промилле</span>
            {{ inspection.hasOwnProperty('alcometer_result') ? inspection.alcometer_result + ' ‰' : 'Неизвестно' }}
          </div>
          <!--        <div v-if="system.thermometer_visible" class="step-result__card animate__animated animate__fadeInUp d-2">-->
          <div class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Температура тела</span>
            {{ inspection.hasOwnProperty('t_people') ? inspection.t_people + ' °C' : 'Неизвестно' }}
          </div>
          <!--        <div v-if="system.question_sleep" class="step-result__card animate__animated animate__fadeInUp d-2">-->
          <div class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Сонливость</span>
            {{ inspection.hasOwnProperty('sleep_status') ? getSleepStatus(inspection.sleep_status) : 'Неизвестно' }}
          </div>
          <!--        <div v-if="system.question_helth" class="step-result__card animate__animated animate__fadeInUp d-2">-->
          <div class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Самочувствие</span>
            {{ inspection.hasOwnProperty('people_status') ? getPeopleStatus(inspection.people_status) : 'Неизвестно' }}
          </div>
        </div>
      </div>
      <div class="step-result__footer">
        <div class="feedback-after-inspection">
          <div v-if="hasReaction">
            <span class="header">Спасибо за оценку! <br> Ваше мнение важно для нас!</span>
          </div>
          <div v-else>

            <!--          <div v-if="!hasReaction" class="feedback-after-inspection">-->
            <!--          <div v-if="!hasReaction" class="feedback-after-inspection">-->
            <span class="header">Как прошел осмотр?</span>
            <div class="like-box">
              <span class="like bad" @click="saveFeedback('negative')"><img
                  src="@/assets/images/like.svg"></span>
              <span class=" like awesome" @click="saveFeedback('positive')"><img src="@/assets/images/like.svg"></span>
            </div>
          </div>
        </div>
        <!--          <div v-else class="step-result__buttons">-->

        <div class="step-result__buttons">
          <button @click="$router.push('/')" class="btn blue animate__animated animate__fadeInUp">В начало</button>
          <button v-if="this.admitted && this.canRetryPrint"
                  @click="replayPrint()"
                  class="btn opacity animate__animated animate__fadeInUp">Повтор печати
          </button>
          <button v-if="connection && this.admitted && this.canRetryPrintQR"
                  @click="replayPrintQr()"
                  class="btn opacity animate__animated animate__fadeInUp">Повтор печати QR
          </button>
        </div>
      </div>
    </div>
    <div v-if="!loading" class="madam-t7 animate__fadeInUpBig">
      <div class="madam-t7-text-box animate__animated animate__fadeInUp">
        <div class="wish">
          <span class="animate__fadeInUp"> {{ drawReaction }}</span>
        </div>
        <img width="300" src="@/assets/images/madam-t7-say.svg">
      </div>
      <img width="300" height="500" src="@/assets/images/madam-t7.svg" style="margin-top: 20%">
    </div>
  </div>
</template>
