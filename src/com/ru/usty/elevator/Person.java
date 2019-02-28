package com.ru.usty.elevator;

public class Person implements Runnable {

    private int sourceFloor, destinationFloor;

    Person(int sourceFloor, int destinationFloor) {

        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
    }

    @Override
    public void run() {

       try {
           // Hér er verið að bíða á ákveðinni semph. Ef við biðum á
           // Réttri semoph., og hún hleypir okkur inn á réttri hæð,
           // Þá vitum við að þegar við erum komin niður fyrir catch blockina
           // í kóðanum, að þá er það búið að gerast.
           // Person þráðurinn þarf því ekki að tékka á neinu, ef hann komst
           // í gegn þá eru þær aðstæður við lýði sem við erum að leitast eftir

           ElevatorScene.elevatorWaitMutex.acquire();
            ElevatorScene.inSem.acquire(); // Wait
           System.out.println("number og people in ele: " + ElevatorScene.scene.getNumberOfPeopleInElevator(0));
           ElevatorScene.scene.incrementNumberOfPeopleInElevator(0);
           System.out.println("number og people in ele: " + ElevatorScene.scene.getNumberOfPeopleInElevator(0));
           System.out.println("Available permits: " + ElevatorScene.scene.inSem.availablePermits());
           ElevatorScene.elevatorWaitMutex.release();


           // ATH: lyftan þarf einnig að locka þessum Mutex þegar hún er að
           // mæta á hæðina og breyta honum eða þegar hún er að fara á hæðina
           // og breyta honum.

           /* Person þræðirnir kalla bara einu sinni á þennan Mutex en lyftan oft
                sem þýðir að ef hún er í lúppu væri hægt að stinga sér inn og í og eiga
                við breytuna þegar hún er að vinna.
            */

           /* Þegar lyftan kemur á hæð, Person þræðir fara þá út úr lyftunni,
           þá þurfa þeir að decrementa fjöldann í lyftunni, lyftan mætir á hæð,
           kallar á einhver acquire og release. Person þræðirnir, þegar þeir sleppa,
           þá ganga þeir frá fjöldanum í lyftunni eftir sig. Og þegar þeir koma inn þá
           þurfa þeir að hækka fjöldann í lyftunni og allt þetta.

           Allir þessir fjórir hlutir;
           Sem sem hleypa þér inn í lyftuna
           Sem sem hleypir þér út úr lyftunni
           Þurfum ekki endilega sama Mutex á þær er það?

            Sú sem hleypir þér út er væntanlega eitthvað sem PersonCount
            þarf að wait()-a á þegar hann kemst inn í lyftuna.
            Kannski þurfum við einn á sitthvort, en ekki þar með sagt að
            það sé ekki í lagi að nota sama mutex á bæði.
            ATH: viljum nota mutexa víðar heldur en óvíðar, en ekki endilega
            fleiri heldur en færri (færri = öruggara)

            Mutex: Þessi mutex verndar semaphorurnar okkar. Þannig að allar
            counting semaphorurnar okkar eru alltaf acquire-aðar inní sama Mutex

            Það að hækka og lækka semophorur ==> eitt critical section
            og við notum mutex á það.

            T.d. það að incr/decr fjölda fólks á hæð er eitt critical section
            og það þarf að nota sama mutex allir sem gera þetta. Þarf að vera
            sama mutex háð.

            T.d. elevatorCountMutex alltaf þegar er verið að breyta fjölda fólks í
            lyftunum

            ElevatorWaitMutex notaður til að vernda semaphores sem hleypa okkur inn
            í lyfturnar. Hvort við notum þann sama til að hleypa okkur út, gildir einu

            ::: ÞEGAR VIÐ ERUM KOMIN MEÐ FLEIRI LYFTUR:::

            Þá gætum við þurft að bíða á tveimur semophores, einni til að
            bíða eftir að það komi einhver lyfta; áður en sú semophora er losnuð
            þarf einhver að sjá til þess að setja í einhverja breytu hvaða lyfta
            það er sem þú mátt fara inn í. Og þá ferðu inn og waitar eftir annarri
            semophoru fyrir þá lyftu og hún hleypir þér síðan inn.


            */
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Person is through barrier hér

        // Minnkum við röðina
        ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);

        // Hvað þarf þráðurinn að gera næst?
        // Nú er hann kominn inn í lyftu, búinn að ganga frá einhverjum
        // tölum í kringum sig

        // Mögulega þarf hann að hækka einhvern counter sem segir til um
        // Hve margir eru inni í hverri lyftu.

        /* Betra að láta Person þræðina hækka og lækka gildið á fjölda
        fólks inní lyftunni. Af hverju? Því þeir vita fyrir víst að þeir eru
        núna lausir og eru að fara inn í lyftuna.

        Lyftan, þegar hún kallar á release á semoph., þá veit hún ekki fyrir víst
        að það komi raunverulega Person þræðir inn á henni.
        Það sem meira er, hún þarf í raun og veru að nota þetta gildi til að ganga
        frá eftir sig, því ef hún opnar fyrir sex persónum, bara þrjár koma inn, og
        svo fer hún af hæðinni, hvað gerist þá næst þegar það kemur inn persóna á
        þessari hæð?

        Hvað gerir lyfta þegar hún kemur á hæð ef hún er tóm? Kallar á releas() sex sinnum.
        Hvað ef hún er ekki tóm? Þá kallar hún (6 - fólkið í lyftunni) sinnum
        Getum notað getNumberOfPeopleInElevator til þess.

        Áður en hún fer, þá kallar hún á acquire (6 mínus fólkið í lyftunni) sinnum

        Þ.e. þegar ég mæti á hæðina, þá get ég kallað á release() einu sinni
        fyrir hvert laust pláss. Svo breyta Person þræðirnir fjölda lausra plássa.
        Ef fjöldinn er 6 þá kallar hún ekki á acquire.

        Þurfum að hafa Mutex á getNumberOfPeopleInElevator sem lock-ar áður
        en hún byrjar á lúppunni og releasar eftir hana. Person þráður sem kemur
        inn þarf líka að hafa sama Mutex á því að incrementa fjölda fólks í lyftunni,
        eða því yfir höfuð að komast inn.

        Þessi lock á Person þarf að vera áður en hann kallar á 'sem' semophoruna
        Viljum að þegar hann kallar á acquire() fyrir semophoruna að þá verði hann
        látinn bíða því lyftan er að fara. Jafnvel þó það sé pláss fyrir hann.
        Hann var bara of seinn. Hurðin lokuð.

        VISUALIZATION_WAIT_TIME er sá tími sem að þræðirnir hafa á hverri hæð til að
        gera sitt thing (komast inn)
         */




        System.out.println("Person thread released");


    }
}

