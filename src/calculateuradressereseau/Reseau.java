package calculateuradressereseau;
class Reseau extends AdresseIP {
    private String masque;
    private String adresseDebut;
    private String adresseFin;

    private String adresseReseau;
    private String broadcast;
    private int prefixeCIDR;
    private String plageHotesDebut;
    private String plageHotesFin;
    private long nbHotesUtiles;

    public Reseau(String adresse, String masque) throws InvalidIPException {
        super(adresse);
        this.masque = masque;
        calculerPlageAdresse();
    }

    private void calculerPlageAdresse() throws InvalidIPException {
        int[] octets = getOctets();
        int[] masqueBits = convertirMasque(masque);

        prefixeCIDR = calculerPrefixeCIDR(masqueBits);

        int[] debut = new int[4];
        int[] fin = new int[4];

        for (int i = 0; i < 4; i++) {
            debut[i] = octets[i] & masqueBits[i];
            fin[i] = debut[i] | ~masqueBits[i] & 0xFF;
        }

        adresseDebut = convertirEnString(debut);
        adresseFin = convertirEnString(fin);

        adresseReseau = adresseDebut;
        broadcast = adresseFin;

        long networkInt = ipToLong(debut);
        long broadcastInt = ipToLong(fin);
        int hostBits = 32 - prefixeCIDR;
        if (hostBits <= 1) {
            nbHotesUtiles = 0;
            plageHotesDebut = "-";
            plageHotesFin = "-";
        } else {
            nbHotesUtiles = (1L << hostBits) - 2;
            plageHotesDebut = longToIp(networkInt + 1);
            plageHotesFin = longToIp(broadcastInt - 1);
        }
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

    private int calculerPrefixeCIDR(int[] masqueBits) throws InvalidIPException {
        int prefix = 0;
        boolean zeroFound = false;
        for (int i = 0; i < 4; i++) {
            int b = masqueBits[i];
            for (int bit = 7; bit >= 0; bit--) {
                boolean isOne = ((b >> bit) & 1) == 1;
                if (isOne) {
                    if (zeroFound) {
                        throw new InvalidIPException("Masque invalide.");
                    }
                    prefix++;
                } else {
                    zeroFound = true;
                }
            }
        }
        return prefix;
    }

    private long ipToLong(int[] octets) {
        return ((long) (octets[0] & 0xFF) << 24)
                | ((long) (octets[1] & 0xFF) << 16)
                | ((long) (octets[2] & 0xFF) << 8)
                | ((long) (octets[3] & 0xFF));
    }

    private String longToIp(long value) {
        long o1 = (value >> 24) & 0xFF;
        long o2 = (value >> 16) & 0xFF;
        long o3 = (value >> 8) & 0xFF;
        long o4 = value & 0xFF;
        return o1 + "." + o2 + "." + o3 + "." + o4;
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

    public String getAdresseReseau() {
        return adresseReseau;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public int getMasqueCIDR() {
        return prefixeCIDR;
    }

    public String getPlageHotesDebut() {
        return plageHotesDebut;
    }

    public String getPlageHotesFin() {
        return plageHotesFin;
    }

    public long getNbHotesUtiles() {
        return nbHotesUtiles;
    }
}
