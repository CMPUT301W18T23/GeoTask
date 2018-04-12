package com.geotask.myapplication;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Photo;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class LetThereBeRealisticData {

    @Test
    public void data() throws IOException {
        ElasticsearchController controller = new ElasticsearchController();
        controller.verifySettings();
        LocalDataBase dataBase = LocalDataBase.getDatabase(InstrumentationRegistry.getTargetContext());
        dataBase.userDAO().delete();
        dataBase.bidDAO().delete();
        dataBase.taskDAO().delete();

        try {
            controller.deleteIndex();
            controller.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String email[] = new String[30];
        String name[] = new String[30];
        String phoneNumber[] = new String[30];

        String generated = "Kanisha Broady  \n" +
                "Cherly Pair  \n" +
                "Lajuana Peavey  \n" +
                "Miles Donegan  \n" +
                "Teisha Noga  \n" +
                "Adriane Ayon  \n" +
                "Mattie Tobia  \n" +
                "Arnold Bland  \n" +
                "Gilda Braddock  \n" +
                "Marilyn Beveridge  \n" +
                "Alvina Marotz  \n" +
                "Chanelle Beier  \n" +
                "Asley Keen  \n" +
                "Kathy Tawney  \n" +
                "Doris Vanvalkenburg  \n" +
                "Felipe Crosson  \n" +
                "Tomika Linton  \n" +
                "Maximina Swaney  \n" +
                "Yolande Krull  \n" +
                "Aide Delee  \n" +
                "Abdul Linden  \n" +
                "Nilda Villafane  \n" +
                "Chin Ivers  \n" +
                "Shanna Orona  \n" +
                "Kyla Glidewell  \n" +
                "Cammy Schoenberg  \n" +
                "Amada Hensley  \n" +
                "Floyd Becker  \n" +
                "Abby Chauncey  \n" +
                "Katharina Grund  \n" +
                "\n";
        generated = generated.toLowerCase();
        String phoneGen = "(725) 950-2919\n" +
                "(817) 915-1817\n" +
                "(145) 805-4527\n" +
                "(847) 890-7648\n" +
                "(979) 918-6503\n" +
                "(760) 465-1864\n" +
                "(131) 559-7075\n" +
                "(625) 621-6640\n" +
                "(863) 386-3510\n" +
                "(321) 181-5762\n" +
                "(955) 706-6247\n" +
                "(355) 493-6815\n" +
                "(397) 245-4063\n" +
                "(304) 672-0290\n" +
                "(173) 810-2278\n" +
                "(275) 436-4259\n" +
                "(615) 756-7544\n" +
                "(384) 569-2848\n" +
                "(423) 833-3526\n" +
                "(424) 839-4822\n" +
                "(739) 410-5300\n" +
                "(712) 228-7573\n" +
                "(354) 323-7621\n" +
                "(354) 294-0617\n" +
                "(838) 755-2795\n" +
                "(411) 981-1585\n" +
                "(231) 651-6854\n" +
                "(997) 582-6454\n" +
                "(991) 346-0021\n" +
                "(881) 507-0519";
        generated = generated.replace("\n","");
        phoneGen = phoneGen.replace("-","");
        List<String> singleName = Arrays.asList(generated.split(" "));
        List<String> phone = Arrays.asList(phoneGen.split(" |\n"));

        for (int i = 0; i < 60; i+=2) {
            name[i/2] = singleName.get(i) + " " + singleName.get(i+1);
            email[i/2] = singleName.get(i) + "@gmail.com";
            phoneNumber[i/2] = "780" + phone.get(i+1);
        }

        String title[] = new String[100];
        String description[] = new String[100];

        String gen[] = new String[5];
        gen[0] = "I need help finding my ";
        gen[1] = "Can someone Help me with My ";
        gen[2] = "can someone clean my ";
        gen[3] = "Urgent! need help with my ";
        gen[4] = "I have an idea for my ";

        String nouns = "candy wrapper\n" +
                "\n" +
                "clothes\n" +
                "\n" +
                "shed\n" +
                "\n" +
                "rusty nail\n" +
                "\n" +
                "photo album\n" +
                "\n" +
                "chalk\n" +
                "\n" +
                "mirror\n" +
                "\n" +
                "doorknob\n" +
                "\n" +
                "candle\n" +
                "\n" +
                "lip gloss\n" +
                "\n" +
                "paper\n" +
                "\n" +
                "playing card\n" +
                "\n" +
                "cork\n" +
                "\n" +
                "bottle cap\n" +
                "\n" +
                "eye liner\n" +
                "\n" +
                "coasters\n" +
                "\n" +
                "thread\n" +
                "\n" +
                "air freshener\n" +
                "\n" +
                "glasses\n" +
                "\n" +
                "checkbook\n" +
                "\n" +
                "toothpaste\n" +
                "\n" +
                "tv\n" +
                "\n" +
                "tree\n" +
                "\n" +
                "boom box\n" +
                "\n" +
                "scotch tape\n" +
                "\n" +
                "paint brush\n" +
                "\n" +
                "apple\n" +
                "\n" +
                "pencil\n" +
                "\n" +
                "box\n" +
                "\n" +
                "rug\n" +
                "\n" +
                "keys\n" +
                "\n" +
                "toe ring\n" +
                "\n" +
                "picture frame\n" +
                "\n" +
                "blouse\n" +
                "\n" +
                "keyboard\n" +
                "\n" +
                "phone\n" +
                "\n" +
                "sun glasses\n" +
                "\n" +
                "television\n" +
                "\n" +
                "headphones\n" +
                "\n" +
                "teddies\n" +
                "\n" +
                "perfume\n" +
                "\n" +
                "lotion\n" +
                "\n" +
                "thermometer\n" +
                "\n" +
                "socks\n" +
                "\n" +
                "milk\n" +
                "\n" +
                "bag\n" +
                "\n" +
                "cinder block\n" +
                "\n" +
                "floor\n" +
                "\n" +
                "credit card\n" +
                "\n" +
                "sofa\n" +
                "\n" +
                "cup\n" +
                "\n" +
                "ice cube tray\n" +
                "\n" +
                "beef\n" +
                "\n" +
                "flowers\n" +
                "\n" +
                "bow\n" +
                "\n" +
                "mp3 player\n" +
                "\n" +
                "chapter book\n" +
                "\n" +
                "bowl\n" +
                "\n" +
                "food\n" +
                "\n" +
                "wallet\n" +
                "\n" +
                "leg warmers\n" +
                "\n" +
                "clay pot\n" +
                "\n" +
                "rubber band\n" +
                "\n" +
                "pool stick\n" +
                "\n" +
                "twister\n" +
                "\n" +
                "water bottle\n" +
                "\n" +
                "glass\n" +
                "\n" +
                "nail clippers\n" +
                "\n" +
                "car\n" +
                "\n" +
                "hair brush\n" +
                "\n" +
                "car\n" +
                "\n" +
                "car\n" +
                "\n" +
                "car\n" +
                "\n" +
                "car\n" +
                "\n" +
                "car\n" +
                "\n" +
                "bracelet\n" +
                "\n" +
                "lace\n" +
                "\n" +
                "creek\n" +
                "\n" +
                "desk\n" +
                "\n" +
                "tire swing\n" +
                "\n" +
                "white out\n" +
                "\n" +
                "stockings\n" +
                "\n" +
                "money\n" +
                "\n" +
                "fake flowers\n" +
                "\n" +
                "shampoo\n" +
                "\n" +
                "grid paper\n" +
                "\n" +
                "ring\n" +
                "\n" +
                "video games\n" +
                "\n" +
                "lamp shade\n" +
                "\n" +
                "zipper\n" +
                "\n" +
                "needle\n" +
                "\n" +
                "house\n" +
                "\n" +
                "book\n" +
                "\n" +
                "table\n" +
                "\n" +
                "sketch pad\n" +
                "\n" +
                "sponge\n" +
                "\n" +
                "pants\n" +
                "\n" +
                "radio\n" +
                "\n" +
                "USB drive\n" +
                "\n" +
                "seat belt\n" +
                "\n";
        List<String> seperated = Arrays.asList(nouns.split("\n"));
        System.out.print(seperated.size());
//        System.out.print(seperated);
        for (int i = 0; i < 200; i += 2) {
            title[i/2]= gen[(i/2)%5]+seperated.get(i);
            switch((i/2)%5){
                case 0:
                    description[i/2]="I lost my "+ seperated.get(i)+ ". Can someone please help me find it? Thanks";
                    break;
                case 1:
                    description[i/2]="I need some work done on my "+ seperated.get(i)+ ".";
                    break;
                case 2:
                    description[i/2]="My "+ seperated.get(i)+ " is dirty. can someone come over and clean if for me?";
                    break;
                case 3:
                    description[i/2]="need a quick response. I lost my "+ seperated.get(i)+ " and need help finding it asap!";
                    break;
                case 4:
                    description[i/2]="I have a good idea to improve my "+ seperated.get(i)+ " but dont have enough time to do it my self. can someone help me?";
                    break;
            }
        }

        ArrayList<User> users = new ArrayList<User>();
        byte[] photobyte = new byte[0];
        ArrayList<byte[]> taskPhoto = new ArrayList<byte[]>();
        Photo photo = new Photo("123", taskPhoto);
        controller.createNewDocument(photo);
        taskPhoto.add(photobyte);
        User master = new User(photobyte, "1111", "1", "111111111111");
        User master2 = new User(photobyte, "2222", "mtang@ualberta.ca", "22222222");

        controller.createNewDocument(master);
        controller.createNewDocument(master2);

        for(int i = 0; i< 30; i++) {
            User a = new User(photobyte, name[i],email[i],phoneNumber[i]);
            controller.createNewDocument(a);
            users.add(a);
        }
//
//        Random r = new Random();
//        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        ArrayList<Task> tasks = new ArrayList<Task>();
        for(int i = 0; i< 100; i++) {
            Random rand = new Random();
            double x;
            double y;
            if (i > 70){
                x = rand.nextInt(10) -5;
                y = rand.nextInt(10) -5;
            }else{
                x = -0.2 + (0.2 - -0.2) * rand.nextDouble();
                y = -0.2 + (0.2 - -0.2) * rand.nextDouble();
            }
            x = x+53.5232;
            y = y+-113.5263;
            String lat = Double.toString(x)+","+Double.toString(y);
            Task a = new Task(users.get(i%30).getObjectID(),title[i],description[i], lat);
//            controller.createNewDocument(a);
            a.setPhotoList(photo.getObjectID());
            tasks.add(a);
            try {
                if (i > 72){
                    int value = rand.nextInt(100)+1;
                    Bid bid = new Bid(users.get((i%29)+1).getObjectID() , (double) value, a.getObjectID());
                    controller.createNewDocument(bid);
                    a.setStatusBidded();
                }
                    controller.createNewDocument(a);

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }



//        User michael = new User("Michael", "mtang@ualberta.ca", "5555555");
//        User kyle = new User("Kyle", "k@k", "55552355");
//        User JamesJ = new User("JamesJ", "1@1.1", "2353490423");
//        User Kehan = new User("kehan", "kehan1@u", "2348793258");
//        Task task;
//        controller.createNewDocument(michael);
//        controller.createNewDocument(kyle);
//        controller.createNewDocument(JamesJ);
//        controller.createNewDocument(Kehan);
//        String temp;
//        byte[] photo = new byte[0];
//        for(int i = 0; i< 100; i++){
//            for(int j = 0; j< 5; j++){
//                temp = "test" + i + "block" + j;
//
//                task = new Task(temp, temp, temp);
//                task.setRequesterID(michael.getObjectID());
//                try {
//                    controller.createNewDocument(task);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}
