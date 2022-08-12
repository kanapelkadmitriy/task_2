package com.example.task_2.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class CommonBalance extends BaseEntity {

    private Long accountId;

    @Embedded
    private OuterBalance outerBalance;

    @Embedded
    private InnerBalance innerBalance;

    @Embedded
    private MoneyTurnover moneyTurnover;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_balance_id")
    private SummaryBalance summaryBalance;
}
