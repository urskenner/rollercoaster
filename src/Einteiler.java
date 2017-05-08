import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * sCreated by Jojo on 01.05.17.
 */
public class Einteiler {

    private String status = "free";
    private SingleRiderSchlange singleRiderSchlange;
    private MultiRiderSchlange multiRiderSchlange;
    private Zug zug;

    public Einteiler(SingleRiderSchlange singleRiderSchlange, MultiRiderSchlange multiRiderSchlange, Zug zug) {
        this.singleRiderSchlange = singleRiderSchlange;
        this.multiRiderSchlange = multiRiderSchlange;
        this.zug = zug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatusFree() {
        this.status = "free";
    }

    public void setStatusTaken() {
        this.status = "taken";
    }


    public void fillTrain() {

        if (multiRiderSchlange.isEmpty() == false) {
            if (zug.getStatus().equals("green") && this.status.equals("free") && multiRiderSchlange.getWartelaenge() <= 100) {
                System.out.println("Unter 100");
                System.out.println(multiRiderSchlange);
                this.setStatusTaken();
                fillBelowHundretOne();
            }

            else if (zug.getStatus().equals("green") && this.status.equals("free") && multiRiderSchlange.getWartelaenge() >= 100) {
                System.out.println("Über 100");
                this.setStatusTaken();
                fillOverHundert();
            } else {

                zug.setStatusRed();
                //Zug fährt zu oder ist Beschädigt Methode
            }
        } else{
        }
    }

    //Gruppe passt exakt in den Wagen

    private void groupFitInExakt() {

        multiRiderSchlange.removePersons();
        zug.setTakenSeats(0);

        this.setStatusFree();
        System.out.println(zug.getRestFreeSeats());
        System.out.println("------------");
        zug.setAktiv();
        try {
            Thread.sleep(2000);
            fillTrain();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void groupFitInLess() {
        int freeSeats = zug.getTakenSeats()[zug.getAktiv()] - multiRiderSchlange.getFirst().getGruppengroeße();
        System.out.println("Free: " + freeSeats);
        zug.setTakenSeats(freeSeats);
        System.out.println(Arrays.toString(zug.getTakenSeats()));
        this.setStatusFree();
        multiRiderSchlange.removePersons();

        System.out.println(zug.getRestFreeSeats());
        System.out.println("------------");
        try {
            Thread.sleep(3000);
            fillTrain();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void trainReady() {
        zug.setStatusYellow();
        fillTrain();
    }

    private void newDeploy() {
        zug.setAktiv();
        this.setStatusFree();
        fillTrain();
    }

    private void restGroupDeploy() {
        zug.setTakenSeats(0);
        multiRiderSchlange.getFirst().setGruppengroeße(multiRiderSchlange.getFirst().getGruppengroeße() - zug.getTakenSeats()[zug.getAktiv()]);
        zug.setAktiv();
        this.setStatusFree();
        fillTrain();
    }

    private void groupFitafterDeploy() {

        int val = multiRiderSchlange.getFirst().getGruppengroeße() - (zug.getTakenSeats()[zug.getAktiv()] - 1);
        zug.setTakenSeats(zug.getTakenSeats()[zug.getAktiv()]-val);
        multiRiderSchlange.getFirst().setGruppengroeße(val);
        zug.setAktiv();
        this.setStatusFree();
        fillTrain();
    }

    private void fillBelowHundretOne() {

        if (zug.getAktiv() <= zug.getWaggons()) {

            //Gruppe passt Exakt in einen Waggon
            if (multiRiderSchlange.getFirst().getGruppengroeße() == zug.getTakenSeats()[zug.getAktiv()]) {
                groupFitInExakt();
            }

            // Gruppe passt in einen Wagon
            else if (multiRiderSchlange.getFirst().getGruppengroeße() < zug.getTakenSeats()[zug.getAktiv()]) {
                groupFitInLess();
            }

            // Gruppe passt nicht in einen Wagon
            else if (multiRiderSchlange.getFirst().getGruppengroeße() > zug.getTakenSeats()[zug.getAktiv()]) {
                //Es gib weniger freie Sitze als benötigt
                if (multiRiderSchlange.getFirst().getGruppengroeße() > zug.getRestFreeSeats()) {
                    System.out.println(zug.getRestFreeSeats());
                    trainReady();
                }

                // es gibt genügend Sitze aber es bleibt ein einzelner übrig
                else if (multiRiderSchlange.getFirst().getGruppengroeße() < zug.getRestFreeSeats()) {
                    if (zug.getTakenSeats()[zug.getAktiv()] <= 1) {
                        System.out.println("Freie Sitze: " + zug.getRestFreeSeats());
                        System.out.println("Test");
                        newDeploy();

                    } else if (multiRiderSchlange.getFirst().getGruppengroeße() < zug.getRestFreeSeats()) {

                        if ((multiRiderSchlange.getFirst().getGruppengroeße() - zug.getTakenSeats()[zug.getAktiv()]) %
                                zug.getAnzahl_sitze() == 0 || (multiRiderSchlange.getFirst().getGruppengroeße() -
                                zug.getTakenSeats()[zug.getAktiv()]) % zug.getAnzahl_sitze() == 2) {
                            System.out.println("Test 2");

                            restGroupDeploy();

                        } else if ((multiRiderSchlange.getFirst().getGruppengroeße() - zug.getTakenSeats()[zug.getAktiv()]) %
                                zug.getAnzahl_sitze() == 1) {

                            if (multiRiderSchlange.getFirst().getGruppengroeße() % (zug.getTakenSeats()[zug.getAktiv()]
                                    - 1) == 0 || multiRiderSchlange.getFirst().getGruppengroeße() % (zug.getTakenSeats()[zug.getAktiv()] - 1) == 2) {
                                groupFitafterDeploy();
                            }

                        }


                    }
                }

            }
        }
    }

    private void fillOverHundert() {

        if (zug.getAktiv() <= zug.getWaggons()) {

            //Gruppe passt Exakt in einen Waggon
            if (multiRiderSchlange.getFirst().getGruppengroeße() == zug.getTakenSeats()[zug.getAktiv()]) {
                groupFitInExakt();
            }

            // Gruppe passt in einen Wagon
            else if (multiRiderSchlange.getFirst().getGruppengroeße() < zug.getTakenSeats()[zug.getAktiv()]) {
                multiRiderSchlange.removePersons();
                int freeSeats = zug.getTakenSeats()[zug.getAktiv()] - multiRiderSchlange.getFirst().getGruppengroeße();
                zug.setTakenSeats(freeSeats);
                this.setStatusFree();
                fillTrain();
            }

            // Gruppe passt nicht in einen Wagon
            else if (multiRiderSchlange.getFirst().getGruppengroeße() > zug.getTakenSeats()[zug.getAktiv()]) {
                //Es gib weniger freie Sitze als benötigt
                if (multiRiderSchlange.getFirst().getGruppengroeße() > zug.getRestFreeSeats()) {
                    zug.setStatusYellow();
                    fillTrain();
                }

                // es gibt genügend Sitze aber es bleibt ein einzelner übrig
                else if (multiRiderSchlange.getFirst().getGruppengroeße() < zug.getRestFreeSeats()) {
                    if (zug.getTakenSeats()[zug.getAktiv()] <= 1) {
                        zug.setAktiv();
                        this.setStatusFree();
                        fillTrain();
                    } else if (multiRiderSchlange.getFirst().getGruppengroeße() < zug.getRestFreeSeats()) {


//                        if ()//Ausgelagerte Methode muss hier hin


                        multiRiderSchlange.getFirst().setGruppengroeße(multiRiderSchlange.getFirst().getGruppengroeße() - zug.getTakenSeats()[zug.getAktiv()]);
                        zug.setAktiv();
                        this.setStatusFree();
                        fillTrain();
                    }

                }
                //Nach dem die Wagons mit den Gruppen zugeilt wurden und die Warteschlangelänge über 100 ist, können wir via Singlerider die freien Sitze auffüllen.
                fillsingle();
            }
        }
    }


    private void fillsingle() {

        if (zug.getAktiv() <= zug.getWaggons() && singleRiderSchlange.getWartelaenge() > 0 && zug.getRestFreeSeats() > 0) {

            //SingleRider passt genau in aktiven Wagon
            if (singleRiderSchlange.getFirst().getGruppengroeße() == zug.getTakenSeats()[zug.getAktiv()]) {
                singleRiderSchlange.removePersons();
                zug.setTakenSeats(0);
                zug.setAktiv();
                this.setStatusFree();
                fillsingle();
            } else {
                zug.setAktiv();
                fillsingle();
            }

        } else {
            zug.setStatusYellow();
            fillTrain();
        }
    }
}



