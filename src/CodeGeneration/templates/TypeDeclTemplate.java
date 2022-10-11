import java.util.ArrayList;
import java.util.List;

public class TypeDeclTemplate implements Template {
    String result;

    public TypeDeclTemplate(Template type, List<Template> arrayDeclIDs) {
        result = String.format("typedef %s ", type.getOutput());

        for (var arrayDeclIDsItem : arrayDeclIDs) {
            result += arrayDeclIDsItem.getOutput() + ", ";
        }

        result = result.replaceFirst(", $", ";");
    }

    @Override
    public String getOutput() {
        return result;
    }
}
