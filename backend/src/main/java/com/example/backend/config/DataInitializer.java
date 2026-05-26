package com.example.backend.config;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            ServicePackageRepository servicePackageRepository,
            PetRepository petRepository,
            AppointmentRepository appointmentRepository,
            ProcessRepository processRepository,
            MemorialRepository memorialRepository,
            MessageRepository messageRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                System.out.println("数据库已有数据，跳过初始化");
                return;
            }

            System.out.println("开始初始化测试数据...");

            String encodedPassword = passwordEncoder.encode("123456");

            List<User> users = Arrays.asList(
                    createUser("admin", encodedPassword, "系统管理员", "admin@petfuneral.com", 30, User.Role.ADMIN, User.Status.ACTIVE),
                    createUser("zhangsan", encodedPassword, "张三", "zhangsan@example.com", 28, User.Role.USER, User.Status.ACTIVE),
                    createUser("lisi", encodedPassword, "李四", "lisi@example.com", 35, User.Role.USER, User.Status.ACTIVE),
                    createUser("wangwu", encodedPassword, "王五", "wangwu@example.com", 42, User.Role.USER, User.Status.ACTIVE),
                    createUser("zhaoliu", encodedPassword, "赵六", "zhaoliu@example.com", 25, User.Role.USER, User.Status.DISABLED)
            );
            userRepository.saveAll(users);
            System.out.println("✓ 用户数据初始化完成");

            List<ServicePackage> packages = Arrays.asList(
                    createServicePackage("基础火化套餐", "cremation", "为您的爱宠提供体面、庄重的火化服务，包含独立火化和骨灰盒。", new BigDecimal("1280.00"), new BigDecimal("1580.00"), "独立火化服务,精美骨灰盒,火化证书,专车接送", 1),
                    createServicePackage("标准告别套餐", "farewell", "包含告别仪式、鲜花布置、火化服务及纪念品，让告别更有仪式感。", new BigDecimal("2980.00"), new BigDecimal("3580.00"), "告别仪式场地,鲜花布置,独立火化,精美骨灰盒,纪念相册,火化证书,专车接送", 2),
                    createServicePackage("尊享纪念套餐", "memorial", "全方位的纪念服务，包含告别仪式、火化、骨灰寄存及定制纪念品。", new BigDecimal("5680.00"), new BigDecimal("6880.00"), "专属告别厅,鲜花花坛布置,独立火化,高档骨灰盒,骨灰寄存一年,定制纪念品,纪念视频,火化证书,专车接送,后续关怀服务", 3),
                    createServicePackage("集体火化服务", "cremation", "经济实惠的集体火化服务，适合预算有限的家庭。", new BigDecimal("380.00"), null, "集体火化服务,基础骨灰袋", 4),
                    createServicePackage("宠物安葬服务", "burial", "提供宠物墓地安葬服务，让爱宠有一个安静的归宿。", new BigDecimal("3980.00"), new BigDecimal("4980.00"), "宠物墓地(10年),墓碑刻字,鲜花祭品,安葬仪式,后续祭扫服务", 5),
                    createServicePackage("骨灰寄存服务", "memorial", "专业的骨灰寄存服务，环境清幽，可随时祭拜。", new BigDecimal("680.00"), null, "骨灰寄存一年,寄存证书,定期祭拜提醒", 6),
                    createServicePackage("上门接宠服务", "other", "24小时上门接宠服务，温柔对待每一位小天使。", new BigDecimal("200.00"), null, "24小时响应,专车上门,温柔处理", 7),
                    createServicePackage("纪念视频制作", "memorial", "专业团队制作纪念视频，永久保存美好回忆。", new BigDecimal("880.00"), new BigDecimal("1280.00"), "专业拍摄,视频剪辑,背景音乐,电子相册", 8)
            );
            servicePackageRepository.saveAll(packages);
            System.out.println("✓ 服务套餐数据初始化完成");

            List<Pet> pets = Arrays.asList(
                    createPet(2L, "小白", "dog", "萨摩耶", "male", LocalDate.of(2018, 3, 15), LocalDate.of(2024, 1, 10), "白色", new BigDecimal("28.50"), "活泼可爱的小萨摩耶，陪伴了我们6年。", "亲爱的小白，谢谢你这些年的陪伴，愿你在汪星快乐奔跑。"),
                    createPet(2L, "咪咪", "cat", "英短蓝猫", "female", LocalDate.of(2020, 6, 20), null, "蓝灰色", new BigDecimal("4.20"), "温顺的英短，喜欢晒太阳。", null),
                    createPet(3L, "大黄", "dog", "金毛", "male", LocalDate.of(2015, 8, 10), LocalDate.of(2023, 12, 25), "金黄色", new BigDecimal("32.00"), "忠诚的大金毛，陪伴了8年多。", "大黄，你是我最好的朋友，永远怀念你。"),
                    createPet(3L, "小花", "cat", "三花猫", "female", LocalDate.of(2019, 2, 14), LocalDate.of(2024, 2, 1), "三花色", new BigDecimal("3.80"), "流浪猫收养，性格亲人。", "小花，虽然你离开了，但你的温暖永远留在我们心中。"),
                    createPet(4L, "豆豆", "dog", "柯基", "male", LocalDate.of(2021, 4, 5), null, "黄白相间", new BigDecimal("12.50"), "小短腿柯基，非常可爱。", null),
                    createPet(4L, "雪球", "cat", "布偶猫", "female", LocalDate.of(2022, 1, 1), null, "白色", new BigDecimal("5.00"), "漂亮的布偶猫，像小公主一样。", null),
                    createPet(4L, "小黑", "dog", "拉布拉多", "male", LocalDate.of(2016, 11, 20), LocalDate.of(2024, 1, 20), "黑色", new BigDecimal("30.00"), "忠诚的黑色拉布拉多。", "小黑，谢谢你守护我们7年多。")
            );
            petRepository.saveAll(pets);
            System.out.println("✓ 宠物数据初始化完成");

            List<Appointment> appointments = Arrays.asList(
                    createAppointment("APT20240110001", 2L, 1L, 2L, LocalDateTime.of(2024, 1, 12, 10, 0), "张三", "13800138001", "北京市朝阳区幸福路88号", "请安排上午的时间", "completed", 1L),
                    createAppointment("APT20240125001", 3L, 3L, 1L, LocalDateTime.of(2024, 1, 28, 14, 0), "李四", "13900139002", "上海市浦东新区花园路66号", "狗狗体型较大，请安排大车", "completed", 1L),
                    createAppointment("APT20240201001", 3L, 4L, 3L, LocalDateTime.of(2024, 2, 3, 9, 0), "李四", "13900139002", "上海市浦东新区花园路66号", null, "completed", 1L),
                    createAppointment("APT20240120001", 4L, 7L, 2L, LocalDateTime.of(2024, 1, 22, 15, 0), "王五", "13700137003", "广州市天河区阳光大道128号", "希望能安排周末", "processing", 1L),
                    createAppointment("APT20240305001", 2L, 2L, 4L, LocalDateTime.of(2024, 3, 8, 11, 0), "张三", "13800138001", "北京市朝阳区幸福路88号", "猫咪比较胆小，请温柔对待", "confirmed", null),
                    createAppointment("APT20240310001", 4L, 5L, 1L, LocalDateTime.of(2024, 3, 12, 16, 0), "王五", "13700137003", "广州市天河区阳光大道128号", null, "pending", null),
                    createAppointment("APT20240315001", 4L, 6L, 6L, LocalDateTime.of(2024, 3, 18, 10, 0), "王五", "13700137003", "广州市天河区阳光大道128号", "需要上门服务", "pending", null)
            );
            appointmentRepository.saveAll(appointments);
            System.out.println("✓ 预约数据初始化完成");

            List<ProcessStage> processes = Arrays.asList(
                    createProcess(1L, "confirmed", "completed", 1L, "已与客户确认预约信息", LocalDateTime.of(2024, 1, 10, 9, 0), LocalDateTime.of(2024, 1, 10, 9, 30)),
                    createProcess(1L, "pickup", "completed", 1L, "已从客户家中接走宠物", LocalDateTime.of(2024, 1, 12, 8, 0), LocalDateTime.of(2024, 1, 12, 9, 0)),
                    createProcess(1L, "farewell", "completed", 1L, "告别仪式已完成", LocalDateTime.of(2024, 1, 12, 10, 0), LocalDateTime.of(2024, 1, 12, 11, 30)),
                    createProcess(1L, "cremation", "completed", 1L, "火化服务已完成", LocalDateTime.of(2024, 1, 12, 12, 0), LocalDateTime.of(2024, 1, 12, 14, 0)),
                    createProcess(1L, "processing", "completed", 1L, "骨灰处理完成，已装入骨灰盒", LocalDateTime.of(2024, 1, 12, 14, 30), LocalDateTime.of(2024, 1, 12, 15, 0)),
                    createProcess(1L, "memorial", "completed", 1L, "纪念品制作完成", LocalDateTime.of(2024, 1, 12, 15, 30), LocalDateTime.of(2024, 1, 12, 16, 30)),
                    createProcess(1L, "completed", "completed", 1L, "服务已全部完成，骨灰已交付客户", LocalDateTime.of(2024, 1, 12, 17, 0), LocalDateTime.of(2024, 1, 12, 17, 30)),
                    createProcess(4L, "confirmed", "completed", 1L, "已确认预约", LocalDateTime.of(2024, 1, 20, 11, 0), LocalDateTime.of(2024, 1, 20, 11, 30)),
                    createProcess(4L, "pickup", "completed", 1L, "已接走宠物", LocalDateTime.of(2024, 1, 22, 14, 0), LocalDateTime.of(2024, 1, 22, 15, 0)),
                    createProcess(4L, "farewell", "processing", 1L, "告别仪式进行中", LocalDateTime.of(2024, 1, 22, 15, 0), null)
            );
            processRepository.saveAll(processes);
            System.out.println("✓ 服务流程数据初始化完成");

            List<MemorialAlbum> albums = Arrays.asList(
                    createMemorial(1L, 2L, "小白的美好时光", "记录小白陪伴我们的点点滴滴，永远怀念你。", 156),
                    createMemorial(3L, 3L, "大黄的八年", "忠诚的大黄，谢谢你八年的陪伴。", 89),
                    createMemorial(4L, 3L, "小花的故事", "从流浪到家人，感谢你选择我们。", 67),
                    createMemorial(7L, 4L, "小黑的守护", "七年守护，一生怀念。", 45)
            );
            memorialRepository.saveAll(albums);
            System.out.println("✓ 纪念相册数据初始化完成");

            List<Message> messages = Arrays.asList(
                    createMessage(1L, 3L, "李四", "小白看起来真的很可爱，愿它在汪星快乐。"),
                    createMessage(1L, 4L, "王五", "萨摩耶的笑容最治愈了，节哀。"),
                    createMessage(1L, null, "匿名用户", "看着照片就想起了我家的狗狗，它们一定在汪星成为了好朋友。"),
                    createMessage(2L, 2L, "张三", "金毛是最忠诚的伙伴，大黄一定很爱你。"),
                    createMessage(2L, 4L, "王五", "八年的陪伴，是多么珍贵的缘分。"),
                    createMessage(3L, 2L, "张三", "流浪猫也能拥有温暖的家，感谢你们的爱心。"),
                    createMessage(4L, 2L, "张三", "黑色拉布拉多真的很帅气，愿它安息。"),
                    createMessage(4L, 3L, "李四", "七年的守护，永远的怀念。")
            );
            messageRepository.saveAll(messages);
            System.out.println("✓ 留言数据初始化完成");

            System.out.println("========================================");
            System.out.println("测试数据初始化完成！");
            System.out.println("用户: admin / 123456 (管理员)");
            System.out.println("用户: zhangsan / 123456 (普通用户)");
            System.out.println("用户: lisi / 123456 (普通用户)");
            System.out.println("用户: wangwu / 123456 (普通用户)");
            System.out.println("========================================");
        };
    }

    private User createUser(String username, String password, String name, String email, int age, User.Role role, User.Status status) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }

    private ServicePackage createServicePackage(String name, String type, String description, BigDecimal price, BigDecimal originalPrice, String includes, int sortOrder) {
        ServicePackage pkg = new ServicePackage();
        pkg.setName(name);
        pkg.setType(type);
        pkg.setDescription(description);
        pkg.setPrice(price);
        pkg.setOriginalPrice(originalPrice);
        pkg.setIncludes(includes);
        pkg.setSortOrder(sortOrder);
        pkg.setStatus(1);
        return pkg;
    }

    private Pet createPet(Long userId, String name, String type, String breed, String gender, LocalDate birthday, LocalDate passDate, String color, BigDecimal weight, String description, String memorialText) {
        Pet pet = new Pet();
        pet.setUserId(userId);
        pet.setName(name);
        pet.setType(type);
        pet.setBreed(breed);
        pet.setGender(gender);
        pet.setBirthday(birthday);
        pet.setPassDate(passDate);
        pet.setColor(color);
        pet.setWeight(weight);
        pet.setDescription(description);
        pet.setMemorialText(memorialText);
        return pet;
    }

    private Appointment createAppointment(String orderNo, Long userId, Long petId, Long packageId, LocalDateTime appointmentTime, String contactName, String contactPhone, String address, String remark, String status, Long operatorId) {
        Appointment apt = new Appointment();
        apt.setOrderNo(orderNo);
        apt.setUserId(userId);
        apt.setPetId(petId);
        apt.setPackageId(packageId);
        apt.setAppointmentTime(appointmentTime);
        apt.setContactName(contactName);
        apt.setContactPhone(contactPhone);
        apt.setAddress(address);
        apt.setRemark(remark);
        apt.setStatus(status);
        apt.setOperatorId(operatorId);
        return apt;
    }

    private ProcessStage createProcess(Long appointmentId, String stage, String status, Long operatorId, String description, LocalDateTime startTime, LocalDateTime endTime) {
        ProcessStage process = new ProcessStage();
        process.setAppointmentId(appointmentId);
        process.setStage(stage);
        process.setStatus(status);
        process.setOperatorId(operatorId);
        process.setDescription(description);
        process.setStartTime(startTime);
        process.setEndTime(endTime);
        return process;
    }

    private MemorialAlbum createMemorial(Long petId, Long userId, String title, String description, int viewCount) {
        MemorialAlbum album = new MemorialAlbum();
        album.setPetId(petId);
        album.setUserId(userId);
        album.setTitle(title);
        album.setDescription(description);
        album.setViewCount(viewCount);
        album.setIsPublic(1);
        return album;
    }

    private Message createMessage(Long albumId, Long userId, String authorName, String content) {
        Message message = new Message();
        message.setAlbumId(albumId);
        message.setUserId(userId);
        message.setAuthorName(authorName);
        message.setContent(content);
        return message;
    }
}
