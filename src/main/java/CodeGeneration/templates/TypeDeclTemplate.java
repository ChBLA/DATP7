package CodeGeneration.templates;

import java.util.ArrayList;
import java.util.List;

public class TypeDeclTemplate extends Template {
    public TypeDeclTemplate(Template type, List<Template> arrayDeclIDs) {
        result = String.format("typedef %s ", type);

        for (var arrayDeclIDsItem : arrayDeclIDs) {
            result += arrayDeclIDsItem + ", ";
        }

        result = result.replaceFirst(", $", ";");
    }
}
