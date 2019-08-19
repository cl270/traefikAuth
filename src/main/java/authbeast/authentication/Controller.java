package authbeast.authentication;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@RestController
public class Controller{

    @Autowired
    ResourceLoader resourceLoader;

    private Map<String,String> credentialsMap = new HashMap<>();
    private String[] headerArr;

    @PostConstruct
    private void init(){
        Resource resource=resourceLoader.getResource("classpath:pass.txt");
        try {
            Scanner scanner = new Scanner(resource.getInputStream());
            headerArr = scanner.next().split(",");
            while(true){
                if(!scanner.hasNext()) break;
                String auth = scanner.next();
                String[] autharr = auth.split(",");
                credentialsMap.put(autharr[0], autharr[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/auth")
    public ResponseEntity authenticator(@RequestHeader("X-Auth-User") String user, @RequestHeader("X-Secret") String secret){
        if(credentialsMap.get(user)!= null && credentialsMap.get(user).equals(secret)) return new ResponseEntity(HttpStatus.OK);

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/jquery-2.1.1.min.js")
    public ResponseEntity jsLib(){
        Resource resource=resourceLoader.getResource("classpath:jquery211.js");
        try{
            byte[]out = IOUtils.toByteArray(resource.getInputStream());
            return new ResponseEntity<>(out,HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/new")
    public ResponseEntity newCredential(@RequestHeader("X-Auth-User") String user, @RequestHeader("X-Secret") String secret, @RequestHeader("X-New-User") String newuser, @RequestHeader("X-New-Secret") String newsecret){
        if(authenticator(user, secret).getStatusCodeValue() > 299) return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        try {
            //addToFile(newuser,newsecret);
            credentialsMap.put(newuser, newsecret);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteCredential(@RequestHeader("X-Auth-User") String user, @RequestHeader("X-Secret") String secret, @RequestHeader("X-Delete-User") String deleteuser){
        if(authenticator(user, secret).getStatusCodeValue() > 299) return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        try {
            //updateFile();
            credentialsMap.remove(deleteuser);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/x")
    public ResponseEntity x(){
        return new ResponseEntity(HttpStatus.OK);
    }

/*    private void addToFile(String newuser, String newsecret) throws Exception{
        ICsvMapWriter mapWriter = null;
        mapWriter = new CsvMapWriter(new FileWriter("target/writeWithCsvMapWriter.csv"),
                CsvPreference.STANDARD_PREFERENCE);
        final CellProcessor[] processors = getProcessors();

        Map<String,String> newline = new HashMap<>();
        newline.put(newuser, newsecret);
        mapWriter.writeHeader(headerArr);
        mapWriter.write(newline, headerArr, processors);
    }

    private static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(),
                new NotNull()
        };

        return processors;
    }*/
}
