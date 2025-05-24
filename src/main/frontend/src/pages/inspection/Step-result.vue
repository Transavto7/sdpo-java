<script>
import {
  saveInspection,
  replayPrint,
  replayPrintQr, sendVerification, checkVerification, getWishMessage,
} from '@/helpers/api/api';
import ResultRepeat from "@/components/ResultRepeat";
import Loader from "@/components/common/Loader";
import {getSettings} from "@/helpers/settings";
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm.vue";
import {stopMedia} from "@/helpers/camera";

export default {
  components: {InputPersonalNumberForm, Loader, ResultRepeat},
  data() {
    return {
      backTimeout: null,

      verified: false,
      verifyCode: '',
      verifyPending: false,
      verification: {},

      result: {},
      conclusion: {
        admitted: '',
        comments: '',
      },
      notIdentified: 'Не идентифицирован',
      feedback: null,
      phrase: 'Пожалуйста, подтвердите результаты осмотра.',
    }
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
    async sendInspection() {
      let global = this;
      let checker = async function () {
        if (global.$store.state.waitRecordMedia) {
          setTimeout(checker, 1000);
        } else {
          await global.save();
          if (global.autoStart) {
            global.backTimeout = global.setTimeoutAndRedirect();
          }
        }
      }
      setTimeout(checker, 1000);
    },
    async getWishMessage() {
      this.phrase = (await getWishMessage()).wish_message ?? null;
    },
    async save() {
      this.result = await saveInspection();
      console.log(this.result)
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

      if (this.verified) {
        return setTimeout(this.redirectHome, this.system.delay_before_redirect_to_main_page);
      } else {
        return setTimeout(this.setTimeoutAndRedirect, 5000);
      }
    },
    updateVerifyCode(input) {
      this.verifyCode = input;
      this.checkVerificationCode();
    },
    async sendVerify(){
      this.verifyPending = true;
      this.verification = await sendVerification(this.inspection.driver_id)
    },
    async checkVerificationCode() {
      if (this.verifyCode.length === 5) {
        this.verified = await checkVerification(this.verification.id, this.verifyCode);
        if (this.verified) {
          this.$store.state.videoRecording = false;
          await stopMedia();
          console.log("stop media")
          await this.sendInspection();
          await this.getWishMessage();
        }
      }
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
    },
    inspection() {
      return this.$store.state.inspection;
    }
    ,
    system() {
      return this.$store.state.config?.system || {};
    },
    driverPhone() {
      return this.$store.state.driver?.phone || 'Ошибка номера!';
    },
    drawReaction() {
      return this.phrase || "Хорошего дня!";
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
<!--  <loader v-model:loading="loading"/>-->
  <result-repeat v-model:visible="showRetryModal"
                 v-model:message-content="getComment"
                 @accept="redirectRepeat()"
  ></result-repeat>
  <div style="display: flex; ">
    <div class="step-result">
      <div style="text-align: center;">
        <!--    <div class="step-result">-->
        <h3 class="step-result__header_with_result animate__animated animate__fadeInDown" v-if="verified">Результаты осмотра:
          <span class="step-result__text animate__animated animate__fadeInUp">
                {{ result.admitted || 'ожидание ответа' }}<br>
            </span>
        </h3>
        <h3 class="step-result__header_with_result animate__animated animate__fadeInDown" v-if="!verified">
          <span class="step-result__text animate__animated animate__fadeInUp">
                Подписание осмотра<br>
            </span>
        </h3>
        <div class="step-result__cards">
          <div v-if="inspection.hasOwnProperty('tonometer')" class="step-result__card animate__animated animate__fadeInUp d-1">
            <span>Давление</span>
            {{ inspection.hasOwnProperty('tonometer') ? inspection.tonometer : 'Неизвестно' }}
          </div>
          <div v-if="inspection.hasOwnProperty('pulse')" class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Пульс</span>
            {{ inspection.hasOwnProperty('pulse') ? inspection.pulse : 'Неизвестно' }}
          </div>
          <div v-if="inspection.hasOwnProperty('alcometer_result')" class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Количество промилле</span>
            {{ inspection.hasOwnProperty('alcometer_result') ? inspection.alcometer_result + ' ‰' : 'Неизвестно' }}
          </div>
          <div v-if="inspection.hasOwnProperty('t_people')" class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Температура тела</span>
            {{ inspection.hasOwnProperty('t_people') ? inspection.t_people + ' °C' : 'Неизвестно' }}
          </div>
          <div v-if="inspection.hasOwnProperty('sleep_status')" class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Сонливость</span>
            {{ inspection.hasOwnProperty('sleep_status') ? getSleepStatus(inspection.sleep_status) : 'Неизвестно' }}
          </div>
          <div v-if="inspection.hasOwnProperty('people_status')" class="step-result__card animate__animated animate__fadeInUp d-2">
            <span>Самочувствие</span>
            {{ inspection.hasOwnProperty('people_status') ? getPeopleStatus(inspection.people_status) : 'Неизвестно' }}
          </div>
        </div>
      </div>
      <div class="step-result__footer">
        <div class="step-result__buttons" v-if="verified">
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
        <div class="step-result__verify" v-if="verifyPending && !verified">
          <div class="driver-form__input">
          <input type="number"
                 class="animate__animated animate__fadeIn d-5"
                 v-model="verifyCode"/>
          </div>
          <input-personal-number-form
              style="margin-bottom: 10px"
              @password=" (input) => updateVerifyCode(input)"
          />
          SMS для подтверждения отправлено на номер {{ driverPhone }}
        </div>
        <div v-if="!verifyPending">
          <button
              @click="sendVerify"
              class="btn opacity animate__animated animate__fadeInUp">
            Согласен с результатами измерений. Отправить SMS для подтверждения
          </button>
        </div>
      </div>
    </div>
    <div class="madam-t7 animate__fadeInUpBig">
      <div class="madam-t7-text-box animate__animated animate__fadeInUp">
        <div class="wish">
          <span class="animate__fadeInUp"> {{ drawReaction }}</span>
        </div>
        <img width="300" src="@/assets/images/madam-t7-say.svg">
      </div>
      <img width="300" height="500" src="@/assets/images/madam-t7.svg">
    </div>
  </div>
</template>

<style scoped lang="scss">
.step-result__verify{
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>