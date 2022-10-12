package CodeGeneration.templates;

public class TypeTemplate extends Template {


    public TypeTemplate(Template prefix, Template typeID) {
        result = prefix.toString().equals("") ? typeID.toString() : String.format("%s %s", prefix, typeID);
    }

    public TypeTemplate(Template typeID) {
        this(new ManualTemplate(""), typeID);
    }


}
