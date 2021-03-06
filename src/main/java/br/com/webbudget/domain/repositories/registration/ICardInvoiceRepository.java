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
package br.com.webbudget.domain.repositories.registration;

import br.com.webbudget.domain.entities.registration.CardInvoice;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 04/03/2013
 */
@Repository
public interface ICardInvoiceRepository extends EntityRepository<CardInvoice, Long> {

//    /**
//     * 
//     * @param card
//     * @return 
//     */
//    public List<CardInvoice> listByCard(Card card);
//    
//    /**
//     *
//     * @param movement
//     * @return
//     */
//    public CardInvoice findByMovement(Movement movement);
//    
//    /**
//     * 
//     * @param card
//     * @param pageRequest
//     * @return 
//     */
//    public Page<CardInvoice> listByCard(Card card, PageRequest pageRequest);
}
