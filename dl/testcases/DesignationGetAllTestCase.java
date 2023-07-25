import com.thinking.machines.hr.dl.exceptions.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.hr.dl.interfaces.dto.*;
import com.thinking.machines.hr.dl.dao.*;
import com.thinking.machines.hr.dl.dto.*;
import java.util.*;

public class DesignationGetAllTestCase {
    public static void main(String gg[]) {
        try {
            DesignationDAOInterface designationDAO;
            designationDAO = new DesignationDAO();
            Set<DesignationDTOInterface> designations;
            designations = designationDAO.getAll();
            designations.forEach((DesignationDTO) -> {
                System.out.println("Code : " + DesignationDTO.getCode());
                System.out.println("Title : " + DesignationDTO.getTitle());
            });
        } catch (DAOException daoException) {
            System.out.println(daoException.getMessage());
        }
    }
}