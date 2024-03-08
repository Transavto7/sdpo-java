<script>
import VueDatePicker from "@vuepic/vue-datepicker";

export default {
  name: 'PrintIndex',
  components: {VueDatePicker},
  data() {
    return {
      date: new Date(),
      lastInspections: [
        {
          time: "00:00:00",
          date: '01-02-2021 ',
          name: 'Иванов Иван Иванович'
        },
        {
          time: "00:00:00",
          date: '02-03-2022',
          name: 'Иванов Иван Иванович'
        },
        {
          time: "00:00:00",
          date: '03-04-2023',
          name: 'Иванов Иван Иванович'
        }
      ]
    }
  },
  methods: {
    print(id) {

    }
  },
  watch: {},
  computed: {
    dateNow() {
      return new Date();
    },
    firstDayLastMonth () {
      let lastDate = new Date();
      lastDate.setMonth(lastDate.getMonth() - 1, 1);
      return lastDate
    },
    hasInspections() {
      return this.lastInspections.length > 0;
    }
  },
  mounted() {

  }
}
</script>

<template>
  <div class="last-inspection">
    <div class="content-block__calendar">
      <VueDatePicker v-model="date"
                     inline
                     auto-apply
                     :disable-year-select="true"
                     disabled-times
                     locale="ru"
                     :hide-navigation="['month','time', 'hours', 'seconds']"
                     :min-date="firstDayLastMonth"
                     :max-date="dateNow"
                     prevent-min-max-navigation
      />
    </div>
    <div v-if="hasInspections" class="content-block__table">
      <table>
        <thead>
        <tr>
          <th>Время</th>
          <th>Дата</th>
          <th>ФИО</th>
          <th></th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="inspection in this.lastInspections">
          <td>{{ inspection.time }}</td>
          <td>{{ inspection.date }}</td>
          <td>{{ inspection.name }}</td>
          <td><a class="btn icon "> <i class="ri-printer-fill"></i> </a></td>
        </tr>
        </tbody>
      </table>
    </div>
    <div v-if="!hasInspections">
      <div class="driver-form__not-found animate__animated animate__fadeInUp">Ранее осмотры не проводились</div>
    </div>
  </div>
</template>

<style scoped>

</style>