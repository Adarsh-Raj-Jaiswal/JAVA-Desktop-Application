import com.thinking.machines.hr.dl.exceptions.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.hr.dl.interfaces.dto.*;
import com.thinking.machines.hr.dl.dao.*;
import com.thinking.machines.hr.dl.dto.*;

public class DesignationUpdateTestCase {
    public static void main(String gg[]) {
        int code = Integer.parseInt(gg[0]);
        String title = gg[1];
        try {
            DesignationDTOInterface designationDTO;
            designationDTO = new DesignationDTO();
            designationDTO.setCode(code);
            designationDTO.setTitle(title);
            DesignationDAOInterface designationDAO;
            designationDAO = new DesignationDAO();
            designationDAO.update(designationDTO);
            System.out.println("Designation : " + title + " updated with code as : " + designationDTO.getCode());
        } catch (DAOException daoException) {
            System.out.println(daoException.getMessage());
        }
    }
}