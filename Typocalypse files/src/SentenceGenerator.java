import java.util.Random;

public class SentenceGenerator {
    // Difficulty level enum
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty currentDifficulty = Difficulty.EASY;

    // Easy difficulty sentences
    static String[] easySubjects = {
            "the ghost", "a shadow", "the figure",
            "a spirit", "the shape", "a form",
            "the mist", "a person", "someone",
            "a voice", "the face", "a thing",
            "the echo", "a noise", "the dark"
    };

    static String[] easyVerbs = {
            "moves", "comes", "walks", "says", "stands", "looks", "waits",
            "goes", "stays", "floats", "shakes", "turns",
            "sees", "hides", "comes back", "runs", "fades"
    };

    static String[] easyAdverbs = {
            "slowly", "quietly", "softly", "low", "dimly", "calmly",
            "strangely", "silently", "gently", "weirdly",
            "lightly", "far", "oddly", "carefully"
    };

    static String[] easyObjects = {
            "a dark room", "the old door", "an empty hall",
            "the dusty floor", "a broken step", "the foggy glass", "a cracked mirror",
            "the old attic", "the cold cellar",
            "a quiet room", "the garden at night",
            "the tall trees"
    };

    static String[] easyConnectors = {
            "while", "as", "when", "near", "by", "behind", "through", "in", "with", "past",
            "under", "around", "along"
    };

    static String[] easySettings = {
            "the wind blew outside", "the lights blinked a bit",
            "the house was quiet", "the moon came in through a window",
            "shadows moved on the wall",
            "steps echoed somewhere",
            "branches hit the window",
            "a door creaked open",
            "cold air came in"
    };

    // Medium difficulty sentences (original)
    static String[] subjects = {
            "the ghostly man", "a strange figure", "the pale shape",
            "a body with no eyes", "the dark shadow", "a broken priest", "the twisted form", "a blank face",
            "the strange thing", "a cursed one",
            "the hollow face", "a bloody person",
            "the old ghost"
    };

    static String[] verbs = {
            "talks", "shows up", "moves", "scratches", "screams", "sings", "breathes", "shakes", "crawls", "bends", "walks weird",
            "leaks", "bites", "follows", "hurts"
    };

    static String[] adverbs = {
            "creepily", "weirdly", "again and again", "wrongly", "quietly", "like a ritual", "badly", "hard", "wildly",
            "angrily", "hungrily", "scarily"
    };

    static String[] objects = {
            "a marked floor", "the bloody table", "an old coffin",
            "the broken tomb", "a dark hole", "the bone room", "a red room",
            "the lost souls", "a book made of skin",
            "the cursed thing", "a haunted mirror"
    };

    static String[] connectors = {
            "while", "as", "just before", "under", "below", "after", "in", "during", "between",
            "inside", "all over", "within",
            "before", "against"
    };

    static String[] settings = {
            "a red moon was in the sky", "the air smelled bad",
            "the lights shook hard", "a music box played wrong", "dust was everywhere",
            "shadows moved",
            "screams were far away",
            "blood dropped from the roof",
            "chains moved in the dark",
            "whispers got louder"
    };

    // Hard difficulty sentences
    static String[] hardSubjects = {
            "the evil demon", "a monster from space", "the old god",
            "a dead star thing", "the huge beast", "a mind eater", "the space horror", "a weird shape",
            "the world eater", "a big nightmare",
            "the one from beyond", "a time ghost",
            "the soul taker"
    };

    static String[] hardVerbs = {
            "bends", "shows", "changes", "breaks", "hurts", "burns", "eats", "cracks", "twists", "shakes", "blows up",
            "takes apart", "kills", "rises", "changes more",
            "ends", "smashes"
    };

    static String[] hardAdverbs = {
            "wrongly", "weirdly", "in a crazy way", "like a sin", "wildly", "in a bad way", "through space", "not normal", "impossible",
            "too deep", "beyond time", "forever",
            "all over space", "endless"
    };

    static String[] hardObjects = {
            "a broken space hole", "the death star", "a cursed red room",
            "the living building", "a gate that changes minds"
    };

    static String[] hardConnectors = {
            "while also", "as if wrong", "without cause", "past time", "beyond", "changing", "breaking rules in", "ruining space near",
            "bending time in", "across worlds of",
            "through odd places of", "changing life near",
            "breaking time inside"
    };

    static String[] hardSettings = {
            "space was twisted", "reality was bleeding",
            "the rules of the world broke", "time went both ways", "space turned weird",
            "cause and effect broke",
            "places mixed together",
            "worlds cracked into pieces",
            "time looped",
            "minds touched the void"
    };

    public static void main(String[] args) {
        SentenceGenerator generator = new SentenceGenerator();
        for (int i = 0; i < 5; i++) {
            System.out.println(generator.generateSentence());
        }
    }

    public String generateSentence() {
        return generateSentence(this.currentDifficulty);
    }

    public String generateSentence(Difficulty difficulty) {
        Random rand = new Random();
        String subject, verb, adverb, object, connector, setting;

        switch (difficulty) {
            case EASY:
                subject = easySubjects[rand.nextInt(easySubjects.length)];
                verb = easyVerbs[rand.nextInt(easyVerbs.length)];
                adverb = easyAdverbs[rand.nextInt(easyAdverbs.length)];
                object = easyObjects[rand.nextInt(easyObjects.length)];
                connector = easyConnectors[rand.nextInt(easyConnectors.length)];
                setting = easySettings[rand.nextInt(easySettings.length)];
                break;
            case MEDIUM:
                subject = subjects[rand.nextInt(subjects.length)];
                verb = verbs[rand.nextInt(verbs.length)];
                adverb = adverbs[rand.nextInt(adverbs.length)];
                object = objects[rand.nextInt(objects.length)];
                connector = connectors[rand.nextInt(connectors.length)];
                setting = settings[rand.nextInt(settings.length)];
                break;
            case HARD:
                subject = hardSubjects[rand.nextInt(hardSubjects.length)];
                verb = hardVerbs[rand.nextInt(hardVerbs.length)];
                adverb = hardAdverbs[rand.nextInt(hardAdverbs.length)];
                object = hardObjects[rand.nextInt(hardObjects.length)];
                connector = hardConnectors[rand.nextInt(hardConnectors.length)];
                setting = hardSettings[rand.nextInt(hardSettings.length)];
                break;
            default:
                // Default to medium
                subject = subjects[rand.nextInt(subjects.length)];
                verb = verbs[rand.nextInt(verbs.length)];
                adverb = adverbs[rand.nextInt(adverbs.length)];
                object = objects[rand.nextInt(objects.length)];
                connector = connectors[rand.nextInt(connectors.length)];
                setting = settings[rand.nextInt(settings.length)];
        }

        String sentence = capitalizeFirst(subject) + " " + adverb + " " + verb + " from " + object +
                ", " + connector + " " + setting + ".";

        return sentence;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return this.currentDifficulty;
    }

    public Difficulty increaseDifficulty() {
        switch (this.currentDifficulty) {
            case EASY:
                this.currentDifficulty = Difficulty.MEDIUM;
                break;
            case MEDIUM:
                this.currentDifficulty = Difficulty.HARD;
                break;
            case HARD:
                // Already at maximum difficulty
                break;
        }
        return this.currentDifficulty;
    }

    public static String capitalizeFirst(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}