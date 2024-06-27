import alertPath from "@/assets/audio/alert.ogg";
import store from "@/store";

export function playAlert() {
    (new Audio(alertPath)).play();
}

export function startWaitTimerRecordMedia() {
    stopWaitTimerRecordMedia();
    let seconds = 17;
    store.state.waitRecordMedia = true

    store.state.timerRecordMedia = setInterval(() => {
        seconds--;
        console.log('осталось ' + seconds)
        console.log('store.state.waitRecordMedia ' + store.state.waitRecordMedia)
        console.log('store.state.timerRecordMedia ' + store.state.timerRecordMedia)
        if (seconds < 1) {
            stopWaitTimerRecordMedia()
        }
    }, 1000);
}

export function stopWaitTimerRecordMedia() {
    if (store.state.waitRecordMedia) {
        store.state.waitRecordMedia = false
        clearInterval(store.state.timerRecordMedia);
    }

}