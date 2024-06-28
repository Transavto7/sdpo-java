import alertPath from "@/assets/audio/alert.ogg";
import store from "@/store";

export function playAlert() {
    (new Audio(alertPath)).play();
}

export async function startWaitTimerRecordMedia() {
    await stopWaitTimerRecordMedia();
    let seconds = 5;
    store.state.waitRecordMedia = true

    store.state.timerRecordMedia = setInterval(() => {
        seconds--;
        console.log('осталось ' + seconds)
        if (seconds < 1) {
            stopWaitTimerRecordMedia()
        }
    }, 1000);
}

export async function stopWaitTimerRecordMedia() {
    if (store.state.waitRecordMedia) {
        store.state.waitRecordMedia = false
        await clearInterval(store.state.timerRecordMedia);
    }

}