import com.thinking.machines.hr.bl.interfaces.managers.*;
import com.thinking.machines.hr.bl.exceptions.*;
import com.thinking.machines.hr.bl.managers.*;
import java.util.*;

class DesignationManagerGetDesignationCountTestCase {
    public static void main(String gg[]) {
        try {
            DesignationManagerInterface designationManager;
            designationManager = DesignationManager.getDesignationManager();
            int count;
            count = designationManager.getDesignationCount();
            System.out.println("Number of designation : " + count);
        } catch (BLException blException) {
            List<String> properties = blException.getProperties();
            properties.forEach((property) -> {
                System.out.println(blException.getException(property));
            });
        }
    }
}