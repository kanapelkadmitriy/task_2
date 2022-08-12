package com.example.task_2.service;

import com.example.task_2.entity.ClassBalance;
import com.example.task_2.entity.CommonBalance;
import com.example.task_2.entity.Document;
import com.example.task_2.entity.InnerBalance;
import com.example.task_2.entity.MoneyTurnover;
import com.example.task_2.entity.OuterBalance;
import com.example.task_2.entity.SummaryBalance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class XmlDocumentParserService implements DocumentParserService {

    private static final Integer NUM_OF_ROW_WITH_DATE = 2;
    private static final Integer DEFAULT_NUMBER_OF_SHEET = 0;
    private static final Integer START_ROWS_WITH_VALUES = 9;
    private static final String PATTERN_OF_ACCOUNT_ID_FOR_COMMON_BALANCE = "\\d{4}";
    private static final String PATTERN_OF_ACCOUNT_ID_FOR_SUMMARY_BALANCE_1 = "\\d{2}";
    private static final String PATTERN_OF_ACCOUNT_ID_FOR_SUMMARY_BALANCE_2 = "\\d{2}\\.\\d";
    private static final String DATE_PATTERN = "\\d{2}\\.\\d{2}\\.\\d{4}";
    private static final String DATE_SEPARATOR = "\\.";
    private static final String PART_OF_CLASS_FILE_NAME = "КЛАСС(.*)";

    private static final Integer IDX_START_DATE = 0;
    private static final Integer IDX_END_DATE = 1;
    private static final Integer IDX_YEAR = 2;
    private static final Integer IDX_MONTH = 1;
    private static final Integer IDX_DAY = 0;

    private static final Integer IDX_CLASS_NAME = 0;
    private static final Integer IDX_ACCOUNT_ID = 0;
    private static final Integer IDX_INNER_BALANCE_ACTIVE = 1;
    private static final Integer IDX_INNER_BALANCE_PASSIVE = 2;
    private static final Integer IDX_MONEY_TURNOVER_DEBIT = 3;
    private static final Integer IDX_MONEY_TURNOVER_CREDIT = 4;
    private static final Integer IDX_OUTER_BALANCE_ACTIVE = 5;
    private static final Integer IDX_OUTER_BALANCE_PASSIVE = 6;

    /*
    Метод для создания документа
     */
    @Override
    public Document createDocument(MultipartFile file) {
        final Map<Integer, List<String>> valueTable = readDocument(file);
        List<String> stringsWithDate = valueTable.get(NUM_OF_ROW_WITH_DATE);
        List<LocalDate> dates = getDates(stringsWithDate.get(0));

        List<Integer> balanceValues = valueTable.keySet().stream()
                .filter(idx -> idx >= START_ROWS_WITH_VALUES)
                .collect(Collectors.toList());

        List<ClassBalance> classBalances = new ArrayList<>();
        List<SummaryBalance> summaryBalances = new ArrayList<>();
        List<CommonBalance> commonBalances = new ArrayList<>();
        for (Integer idx : balanceValues) {
            List<String> cells = valueTable.get(idx);
            if (cells.get(DEFAULT_NUMBER_OF_SHEET).trim().matches(PATTERN_OF_ACCOUNT_ID_FOR_COMMON_BALANCE)) {
                commonBalances.add(createCommonBalance(valueTable.get(idx)));
            }
            if (cells.get(DEFAULT_NUMBER_OF_SHEET).trim().matches(PATTERN_OF_ACCOUNT_ID_FOR_SUMMARY_BALANCE_1)
                    || cells.get(DEFAULT_NUMBER_OF_SHEET).trim().matches(PATTERN_OF_ACCOUNT_ID_FOR_SUMMARY_BALANCE_2)) {
                List<CommonBalance> commonBalanceList = new ArrayList<>(commonBalances);
                summaryBalances.add(createSummaryBalance(valueTable.get(idx), commonBalanceList));
                commonBalances.clear();
            }
            if (cells.get(DEFAULT_NUMBER_OF_SHEET).trim().matches(PART_OF_CLASS_FILE_NAME)) {
                List<SummaryBalance> summaryBalanceList = new ArrayList<>(summaryBalances);
                classBalances.add(createClassBalance(valueTable.get(idx), summaryBalanceList));
                summaryBalances.clear();
            }
        }
        Document document = Document.builder()
                .documentName(file.getName())
                .startDate(dates.get(IDX_START_DATE))
                .endDate(dates.get(IDX_END_DATE))
                .classBalances(classBalances)
                .build();
        classBalances.forEach(classBalance -> classBalance.setDocument(document));
        return document;
    }

    /*
    Метод для чтения документа
     */
    private Map<Integer, List<String>> readDocument(MultipartFile file) {
        Map<Integer, List<String>> valuesTable = new HashMap<>();

        Workbook workbook = null;
        try {
            workbook = new HSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Sheet sheet = Objects.requireNonNull(workbook).getSheetAt(DEFAULT_NUMBER_OF_SHEET);
        for (Row row : sheet) {
            List<String> cells = new ArrayList<>();
            for (Cell cell : row) {
                cells.add(String.valueOf(cell));
            }
            valuesTable.put(row.getRowNum(), cells);
        }
        return valuesTable;

    }

    /*
    Метод для создания ClassBalance
     */
    private ClassBalance createClassBalance(List<String> cells, List<SummaryBalance> summaryBalances) {
        ClassBalance classBalance = ClassBalance.builder()
                .className(cells.get(IDX_CLASS_NAME))
                .summaryBalances(summaryBalances)
                .build();
        summaryBalances.forEach(summaryBalance -> summaryBalance.setClassBalance(classBalance));
        return classBalance;
    }

    /*
    Метод для создания SummaryBalance
     */
    private SummaryBalance createSummaryBalance(List<String> cells, List<CommonBalance> commonBalances) {
        String accountId = cells.get(IDX_ACCOUNT_ID).trim();
        if (accountId.contains(".")) {
            accountId = accountId.substring(0, accountId.indexOf("."));
        }

        SummaryBalance summaryBalance = SummaryBalance.builder()
                .accountId(Long.parseLong(accountId))
                .commonBalances(commonBalances)
                .build();
        commonBalances.forEach(commonBalance -> commonBalance.setSummaryBalance(summaryBalance));
        return summaryBalance;
    }

    /*
    Метод для создания CommonBalance
     */
    private CommonBalance createCommonBalance(List<String> cells) {
        return CommonBalance.builder()
                .accountId(Long.parseLong(cells.get(IDX_ACCOUNT_ID).trim()))
                .innerBalance(InnerBalance.builder()
                        .innerBalanceActive(Double.parseDouble(cells.get(IDX_INNER_BALANCE_ACTIVE)))
                        .innerBalancePassive(Double.parseDouble(cells.get(IDX_INNER_BALANCE_PASSIVE)))
                        .build())
                .moneyTurnover(MoneyTurnover.builder()
                        .debit(Double.parseDouble(cells.get(IDX_MONEY_TURNOVER_DEBIT)))
                        .credit(Double.parseDouble(cells.get(IDX_MONEY_TURNOVER_CREDIT)))
                        .build())
                .outerBalance(OuterBalance.builder()
                        .outerBalanceActive(Double.parseDouble(cells.get(IDX_OUTER_BALANCE_ACTIVE)))
                        .outerBalancePassive(Double.parseDouble(cells.get(IDX_OUTER_BALANCE_PASSIVE)))
                        .build())
                .build();
    }

    private List<LocalDate> getDates(String source) {
        Pattern datePattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = datePattern.matcher(source);

        List<String> dates = new ArrayList<>(2);
        while (matcher.find()) {
            dates.add(matcher.group());
        }
        String[] startDate = dates.get(0).split(DATE_SEPARATOR);
        String[] endDate = dates.get(1).split(DATE_SEPARATOR);

        return List.of(LocalDate.of(Integer.parseInt(startDate[IDX_YEAR]),
                        Integer.parseInt(startDate[IDX_MONTH]),
                        Integer.parseInt(startDate[IDX_DAY])),
                LocalDate.of(Integer.parseInt(endDate[IDX_YEAR]),
                        Integer.parseInt(endDate[IDX_MONTH]),
                        Integer.parseInt(endDate[IDX_DAY])));
    }
}

