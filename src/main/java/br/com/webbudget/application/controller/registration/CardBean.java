/*
 * Copyright (C) 2015 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.webbudget.application.controller.registration;

import br.com.webbudget.application.components.ViewState;
import br.com.webbudget.application.components.table.Page;
import br.com.webbudget.application.controller.FormBean;
import br.com.webbudget.domain.entities.registration.Card;
import br.com.webbudget.domain.entities.registration.CardType;
import br.com.webbudget.domain.entities.registration.Wallet;
import br.com.webbudget.domain.repositories.registration.CardRepository;
import br.com.webbudget.domain.repositories.registration.WalletRepository;
import br.com.webbudget.domain.services.CardService;
import lombok.Getter;
import org.primefaces.model.SortOrder;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static br.com.webbudget.application.components.NavigationManager.PageType.*;

/**
 * The {@link Card} maintenance routine controller
 *
 * @author Arthur Gregorio
 *
 * @version 1.3.0
 * @since 1.0.0, 06/04/2014
 */
@Named
@ViewScoped
public class CardBean extends FormBean<Card> {

    @Getter
    private List<Wallet> wallets;

    @Inject
    private CardService cardService;

    @Inject
    private CardRepository cardRepository;
    @Inject
    private WalletRepository walletRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        super.initialize();
        this.temporizeHiding(this.getDefaultMessagesComponentId());
    }
    
    /**
     * {@inheritDoc}
     *
     * @param id
     * @param viewState 
     */
    @Override
    public void initialize(long id, ViewState viewState) {
        this.viewState = viewState;
        this.wallets = this.walletRepository.findAllActive();
        this.value = this.cardRepository.findOptionalById(id)
                .orElseGet(Card::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeNavigationManager() {
        this.navigation.addPage(LIST_PAGE, "listCards.xhtml");
        this.navigation.addPage(ADD_PAGE, "formCard.xhtml");
        this.navigation.addPage(UPDATE_PAGE, "formCard.xhtml");
        this.navigation.addPage(DETAIL_PAGE, "detailCard.xhtml");
        this.navigation.addPage(DELETE_PAGE, "detailCard.xhtml");
    }

    /**
     * {@inheritDoc }
     *
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @return
     */
    @Override
    public Page<Card> load(int first, int pageSize, String sortField, SortOrder sortOrder) {
        return this.cardRepository.findAllBy(this.filter.getValue(),
                this.filter.getEntityStatusValue(), first, pageSize);
    }

    /**
     * Send the user to the card statistics page
     *
     * @param id the id of the card to show statistics
     * @return the navigation case
     */
    public String changeToStatistics(long id) {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave() {
        this.cardService.save(this.value);
        this.value = new Card();
        this.addInfo(true, "saved");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate() {
        this.value = this.cardService.update(this.value);
        this.addInfo(true, "updated");
    }

    /**
     * {@inheritDoc}
     * 
     * @return 
     */
    @Override
    public String doDelete() {
        this.cardService.delete(this.value);
        this.addInfoAndKeep("deleted");
        return this.changeToListing();
    }

    /**
     * Helper method to get the types defined in the {@link CardType} enum
     *
     * @return array of types from {@link CardType}
     */
    public CardType[] getCardTypes() {
        return CardType.values();
    }
}
