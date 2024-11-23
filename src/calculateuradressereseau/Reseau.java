package calculateuradressereseau;
class Reseau extends AdresseIP {
    private String masque;
    private String adresseDebut;
    private String adresseFin;

    public Reseau(String adresse, String masque) throws InvalidIPException {
        super(adresse);
        this.masque = masque;
        calculerPlageAdresse();
    }

    private void calculerPlageAdresse() throws InvalidIPException {
        int[] octets = getOctets();
        int[] masqueBits = convertirMasque(masque);

        int[] debut = new int[4];
        int[] fin = new int[4];

        for (int i = 0; i < 4; i++) {
            debut[i] = octets[i] & masqueBits[i];
            fin[i] = debut[i] | ~masqueBits[i] & 0xFF;
        }

        adresseDebut = convertirEnString(debut);
        adresseFin = convertirEnString(fin);
    }

    private int[] convertirMasque(String masque) throws InvalidIPException {
        String[] parties = masque.split("\\.");
        if (parties.length != 4) throw new InvalidIPException("Masque invalide.");

        int[] masqueBits = new int[4];
        for (int i = 0; i < 4; i++) {
            masqueBits[i] = Integer.parseInt(parties[i]);
            if (masqueBits[i] < 0 || masqueBits[i] > 255) {
                throw new InvalidIPException("Octet de masque hors de la plage.");
            }
        }
        return masqueBits;
    }

    private String convertirEnString(int[] octets) {
        return octets[0] + "." + octets[1] + "." + octets[2] + "." + octets[3];
    }

    public String getAdresseDebut() {
        return adresseDebut;
    }

    public String getAdresseFin() {
        return adresseFin;
    }
}
